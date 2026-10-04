import urllib.request
import urllib.parse
import json
import ssl

BACKEND = "http://localhost:8080"
AIML = "http://localhost:8000"
FRONTEND = "http://localhost:5173"

def request_json(url, method="GET", data=None, headers=None):
    if headers is None:
        headers = {}
    
    body = None
    if data is not None:
        body = json.dumps(data).encode("utf-8")
        headers["Content-Type"] = "application/json"
    
    req = urllib.request.Request(url, data=body, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req) as resp:
            content = resp.read().decode("utf-8")
            try:
                return resp.status, json.loads(content)
            except Exception:
                return resp.status, content
    except urllib.error.HTTPError as e:
        content = e.read().decode("utf-8")
        try:
            return e.code, json.loads(content)
        except Exception:
            return e.code, content

def run_tests():
    print("\n=======================================================")
    print("    KABADIWALA FULL SYSTEM INTEGRATION TEST SUITE")
    print("=======================================================\n")
    results = []

    # 1. Backend Health
    code, res = request_json(f"{BACKEND}/api/health")
    ok = code == 200 and res.get("data", {}).get("database") == "CONNECTED"
    results.append(("1. Backend Health & DB Connection", ok, f"HTTP {code}, DB: {res.get('data', {}).get('database')}"))

    # 2. AI-ML Health
    code, res = request_json(f"{AIML}/health")
    ok = code == 200 and res.get("status") == "UP"
    results.append(("2. AI-ML Service Health", ok, f"HTTP {code}, Engine: {res.get('engine')}"))

    # 3. Frontend Web App
    try:
        req = urllib.request.Request(FRONTEND)
        with urllib.request.urlopen(req) as resp:
            body = resp.read().decode("utf-8")
            ok = resp.status == 200 and "Kabadiwala" in body
            results.append(("3. Frontend Web Application Serving", ok, f"HTTP {resp.status}, React app loaded"))
    except Exception as e:
        results.append(("3. Frontend Web Application Serving", False, str(e)))

    # 4. User Logins
    code, user_res = request_json(f"{BACKEND}/api/auth/login", method="POST", data={"email": "user@kabadiwala.com", "password": "User@123"})
    citizen_token = user_res.get("data", {}).get("token") if code == 200 else None
    results.append(("4. Citizen Login (user@kabadiwala.com)", citizen_token is not None, f"JWT received (len={len(citizen_token or '')})"))

    code, col_res = request_json(f"{BACKEND}/api/auth/login", method="POST", data={"email": "collector@kabadiwala.com", "password": "Collector@123"})
    col_token = col_res.get("data", {}).get("token") if code == 200 else None
    results.append(("5. Collector Login (collector@kabadiwala.com)", col_token is not None, f"JWT received (len={len(col_token or '')})"))

    code, adm_res = request_json(f"{BACKEND}/api/auth/login", method="POST", data={"email": "admin@kabadiwala.com", "password": "Admin@123"})
    adm_token = adm_res.get("data", {}).get("token") if code == 200 else None
    results.append(("6. Admin Login (admin@kabadiwala.com)", adm_token is not None, f"JWT received (len={len(adm_token or '')})"))

    # 5. Citizen Endpoints
    citizen_hdr = {"Authorization": f"Bearer {citizen_token}"} if citizen_token else {}
    code, bal_res = request_json(f"{BACKEND}/api/wallet/balance", headers=citizen_hdr)
    bal_data = bal_res.get("data", {}) if code == 200 else {}
    results.append(("7. Citizen Wallet Balance API", code == 200, f"Balance: {bal_data.get('balance')} {bal_data.get('currency')}"))

    code, price_res = request_json(f"{BACKEND}/api/waste/estimate-price", method="POST", data={"category": "PLASTIC", "estimatedWeightKg": 10.0})
    est_price = price_res.get("data", {}).get("estimatedPrice") if code == 200 else None
    results.append(("8. Price Estimator Engine API", code == 200 and est_price is not None, f"Estimated: Rs.{est_price}"))

    code, rew_res = request_json(f"{BACKEND}/api/rewards/catalog")
    rew_count = len(rew_res.get("data", [])) if code == 200 else 0
    results.append(("9. Rewards Marketplace Catalog", code == 200 and rew_count > 0, f"{rew_count} reward items found"))

    # 6. Citizen Books Pickup
    book_payload = {
        "wasteType": "PLASTIC",
        "estimatedWeightKg": 8.5,
        "address": "77 Cyber City, Hyderabad",
        "pickupSlot": "2026-10-02 11:00",
        "notes": "Bottles and containers"
    }
    code, book_res = request_json(f"{BACKEND}/api/pickups/book", method="POST", data=book_payload, headers=citizen_hdr)
    pickup_id = book_res.get("data", {}).get("id") if code in (200, 201) else None
    results.append(("10. Citizen Book Pickup Flow", pickup_id is not None, f"HTTP {code}, Booked Pickup ID: {pickup_id}"))

    # 7. Citizen My Pickups
    code, my_res = request_json(f"{BACKEND}/api/pickups/my-pickups", headers=citizen_hdr)
    pickups_list = my_res.get("data", []) if code == 200 else []
    results.append(("11. Citizen My Pickups Dashboard", len(pickups_list) > 0, f"{len(pickups_list)} active pickups"))

    # 8. Collector Lifecycle
    col_hdr = {"Authorization": f"Bearer {col_token}"} if col_token else {}
    code, avail_res = request_json(f"{BACKEND}/api/collector/pickups/available", headers=col_hdr)
    avail_list = avail_res.get("data", []) if code == 200 else []
    results.append(("12. Collector Available Pickups Feed", code == 200, f"{len(avail_list)} available for pickup"))

    if pickup_id:
        # Step A: Accept Pickup
        code, acc_res = request_json(f"{BACKEND}/api/collector/pickups/{pickup_id}/accept", method="PUT", headers=col_hdr)
        status_after_acc = acc_res.get("data", {}).get("status") if code == 200 else None
        results.append(("13. Collector Accept Pickup", status_after_acc == "ACCEPTED", f"Status: {status_after_acc}"))

        # Step B: On The Way
        code, otw_res = request_json(f"{BACKEND}/api/collector/pickups/{pickup_id}/on-the-way", method="PUT", headers=col_hdr)
        status_after_otw = otw_res.get("data", {}).get("status") if code == 200 else None
        results.append(("14. Collector Mark On-The-Way", status_after_otw == "ON_THE_WAY", f"Status: {status_after_otw}"))

        # Step C: Collected
        code, col_pick_res = request_json(f"{BACKEND}/api/collector/pickups/{pickup_id}/collected", method="PUT", headers=col_hdr)
        status_after_col = col_pick_res.get("data", {}).get("status") if code == 200 else None
        results.append(("15. Collector Collect Waste", status_after_col == "COLLECTED", f"Status: {status_after_col}"))

        # Step D: Verify & Automatic Wallet Payout
        verify_payload = {
            "pickupId": pickup_id,
            "actualWeight": 9.5,
            "wasteCategoryId": 2, # Plastic
            "verificationNotes": "Clean and dry PET plastic bottles",
            "qualityGrade": "A"
        }
        code, v_res = request_json(f"{BACKEND}/api/collector/verify", method="POST", data=verify_payload, headers=col_hdr)
        v_data = v_res.get("data", {}) if code == 200 else {}
        results.append(("16. Doorstep Waste Verification & Wallet Credit", code == 200 and v_data.get("calculatedAmount") is not None, f"Calculated Amount: Rs.{v_data.get('calculatedAmount')}"))

    # 9. Admin Dashboard
    adm_hdr = {"Authorization": f"Bearer {adm_token}"} if adm_token else {}
    code, stats_res = request_json(f"{BACKEND}/api/admin/stats", headers=adm_hdr)
    stats_data = stats_res.get("data", {}) if code == 200 else {}
    results.append(("17. Admin System Analytics Stats", code == 200 and stats_data.get("totalPickups") is not None, f"Pickups: {stats_data.get('totalPickups')}, Pending: {stats_data.get('pendingPickups')}"))

    code, users_res = request_json(f"{BACKEND}/api/admin/users", headers=adm_hdr)
    users_data = users_res.get("data", []) if code == 200 else []
    results.append(("18. Admin User Management Roster", len(users_data) > 0, f"{len(users_data)} registered system users"))



    # Output report
    passed = sum(1 for _, ok, _ in results if ok)
    total = len(results)

    for title, ok, details in results:
        mark = "[PASS]" if ok else "[FAIL]"
        print(f"{mark} {title:.<45} {details}")

    print("\n-------------------------------------------------------")
    print(f"OVERALL RESULT: {passed}/{total} Integration Tests Passed ({passed*100//total}%)")
    print("-------------------------------------------------------\n")

if __name__ == "__main__":
    run_tests()
