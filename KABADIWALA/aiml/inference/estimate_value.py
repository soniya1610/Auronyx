# ============================================
# E-WASTE PRICE ESTIMATOR
# ============================================

PRICE_BASELINE = {
    "per_kg": {
        "Printer": 17.50,
        "Television": 20.00,
        "Battery": 85.00,
        "PCB": 394.08
    },

    "per_piece": {
        "Mobile": 95.00,
        "Television": 150.00,
        "Microwave": 200.00,
        "Washing Machine": 633.77
    }
}


def estimate_value(category, unit):
    """
    Estimate scrap value using real-source median baseline.
    """

    if unit not in PRICE_BASELINE:
        return None

    if category not in PRICE_BASELINE[unit]:
        return None

    return PRICE_BASELINE[unit][category]


# ============================================
# TEST
# ============================================

if __name__ == "__main__":

    category = input("Enter waste category: ")
    unit = input("Enter unit (per_kg/per_piece): ")

    price = estimate_value(category, unit)

    if price is None:
        print("\nPrice data is not available for this category/unit.")
    else:
        print("\n===== PRICE ESTIMATION =====")
        print("Waste Type:", category)
        print("Unit:", unit)
        print(f"Estimated Price: ₹{price:.2f}")