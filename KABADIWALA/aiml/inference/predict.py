import tensorflow as tf
import numpy as np
from pathlib import Path


# ==========================================
# PATHS
# ==========================================

BASE_DIR = Path(__file__).resolve().parent.parent

MODEL_PATH = (
    BASE_DIR
    / "models"
    / "trained"
    / "ewaste_mobilenetv2.keras"
)

CLASS_NAMES_PATH = (
    BASE_DIR
    / "models"
    / "trained"
    / "class_names.txt"
)


# ==========================================
# LOAD MODEL
# ==========================================

model = tf.keras.models.load_model(MODEL_PATH)


# ==========================================
# LOAD CLASS NAMES
# ==========================================

with open(CLASS_NAMES_PATH, "r") as f:
    class_names = [line.strip() for line in f.readlines()]


# ==========================================
# PREDICT IMAGE
# ==========================================

def predict_image(image_path):

    image = tf.keras.utils.load_img(
        image_path,
        target_size=(150, 150)
    )

    image_array = tf.keras.utils.img_to_array(image)

    image_array = np.expand_dims(
        image_array,
        axis=0
    )

    # Preprocessing is already inside
    # the saved MobileNetV2 model
    predictions = model.predict(
        image_array,
        verbose=0
    )

    predicted_index = np.argmax(
        predictions[0]
    )

    predicted_class = class_names[
        predicted_index
    ]

    confidence = (
        predictions[0][predicted_index] * 100
    )

    return predicted_class, confidence


# ==========================================
# TERMINAL TEST
# ==========================================

if __name__ == "__main__":

    import sys

    if len(sys.argv) < 2:
        print(
            "Usage: python inference/predict.py "
            "<image_path>"
        )
        sys.exit(1)

    image_path = sys.argv[1]

    if not Path(image_path).exists():
        print("Error: Image file not found.")
        sys.exit(1)

    predicted_class, confidence = predict_image(
        image_path
    )

    print("\n===================================")
    print("       E-WASTE PREDICTION")
    print("===================================")
    print(
        "Predicted Waste Type:",
        predicted_class
    )
    print(
        "Confidence:",
        f"{confidence:.2f}%"
    )
    print("===================================")