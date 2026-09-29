import sys
from pathlib import Path

# inference folder ko Python path mein add karo
sys.path.append(str(Path(__file__).resolve().parent))

from predict import predict_image
from estimate_value import estimate_value


# Check image path
if len(sys.argv) < 2:
    print("Usage: python inference/classify_waste.py <image_path>")
    sys.exit(1)

image_path = sys.argv[1]

# Check image exists
if not Path(image_path).exists():
    print("Error: Image file not found.")
    sys.exit(1)


# ============================================
# 1. IMAGE CLASSIFICATION
# ============================================

predicted_class, confidence = predict_image(image_path)


# ============================================
# 2. UNIT SELECTION
# ============================================

# Categories for which our dataset has per-piece prices
piece_categories = [
    "Mobile",
    "Microwave",
    "Television",
    "Washing Machine"
]

# Categories for which our dataset has per-kg prices
kg_categories = [
    "Battery",
    "PCB",
    "Printer"
]


if predicted_class in piece_categories:
    unit = "per_piece"

elif predicted_class in kg_categories:
    unit = "per_kg"

else:
    unit = None


# ============================================
# 3. PRICE ESTIMATION
# ============================================

if unit is not None:
    estimated_price = estimate_value(predicted_class, unit)
else:
    estimated_price = None


# ============================================
# 4. FINAL RESULT
# ============================================

print("\n===================================")
print("       E-WASTE ANALYSIS RESULT")
print("===================================")

print("Predicted Waste Type:", predicted_class)
print("Confidence:", f"{confidence:.2f}%")

if estimated_price is not None:

    if unit == "per_kg":
        print("Price Unit: ₹/kg")
    else:
        print("Price Unit: ₹/piece")

    print(f"Estimated Scrap Price: ₹{estimated_price:.2f}")

else:
    print("Price Estimation: Not available")

print("===================================")