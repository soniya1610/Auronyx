import sys
import shutil
import tempfile
from pathlib import Path

# Add inference directory to sys.path
INFERENCE_DIR = Path(__file__).resolve().parent / "inference"
sys.path.append(str(INFERENCE_DIR))

try:
    from fastapi import FastAPI, UploadFile, File
    from fastapi.middleware.cors import CORSMiddleware
    import uvicorn
    from predict import predict_image
    from estimate_value import estimate_value

    app = FastAPI(title="Kabadiwala E-Waste AI Prediction Service")

    app.add_middleware(
        CORSMiddleware,
        allow_origins=["*"],
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    @app.get("/health")
    def health():
        return {"status": "UP", "service": "Kabadiwala-AI-ML", "model": "MobileNetV2-EWaste"}

    @app.post("/ai/predict")
    @app.post("/predict")
    async def predict_waste(file: UploadFile = File(...)):
        suffix = Path(file.filename).suffix or ".jpg"
        with tempfile.NamedTemporaryFile(delete=False, suffix=suffix) as tmp:
            shutil.copyfileobj(file.file, tmp)
            tmp_path = tmp.name

        try:
            predicted_class, confidence = predict_image(tmp_path)
            unit = "per_piece" if predicted_class in ["Mobile", "Microwave", "Television", "Washing Machine"] else "per_kg"
            price = estimate_value(predicted_class, unit) or 50.0

            return {
                "category": "E-Waste",
                "item": predicted_class,
                "confidence": round(confidence / 100.0, 4),
                "condition": "USED",
                "estimatedWeight": 1.5 if unit == "per_kg" else 0.5,
                "priceMin": round(price * 0.85, 2),
                "priceMax": round(price * 1.15, 2)
            }
        finally:
            Path(tmp_path).unlink(missing_ok=True)

    if __name__ == "__main__":
        uvicorn.run(app, host="0.0.0.0", port=8000)

except Exception as err:
    print(f"Note: Standard AI server requires fastapi, uvicorn, tensorflow: {err}")
