import {
    HandLandmarker,
    FilesetResolver
} from "https://cdn.jsdelivr.net/npm/@mediapipe/tasks-vision@0.10.0";

const video = document.getElementById("webcam");
const canvasElement = document.getElementById("output_canvas");
const canvasCtx = canvasElement.getContext("2d");

// Flip the video horizontally using CSS
video.style.transform = "scaleX(-1)";

let handLandmarker;
let runningMode = "VIDEO";
let webcamRunning = true;
let ringImage = new Image(); // Create an Image object
ringImage.src = 'ring.png'; // Set the path to your ring image

navigator.mediaDevices.getUserMedia({ video: true }).then(stream => {
    video.srcObject = stream;
    video.addEventListener("loadeddata", predictWebcam);
}).catch(err => {
    console.error("Error accessing the webcam: ", err);
});

// Load HandLandmarker
const createHandLandmarker = async () => {
    const vision = await FilesetResolver.forVisionTasks(
        "https://cdn.jsdelivr.net/npm/@mediapipe/tasks-vision@0.10.0/wasm"
    );
    handLandmarker = await HandLandmarker.createFromOptions(vision, {
        baseOptions: {
            modelAssetPath: "https://storage.googleapis.com/mediapipe-models/hand_landmarker/hand_landmarker/float16/1/hand_landmarker.task",
            delegate: "GPU"
        },
        runningMode: runningMode,
        numHands: 2
    });

    // Start webcam once handLandmarker is ready
    startWebcam();
};

const startWebcam = () => {
    if (!handLandmarker) {
        console.log("Wait for handLandmarker to load.");
        return;
    }

    navigator.mediaDevices.getUserMedia({ video: true }).then(stream => {
        video.srcObject = stream;
        video.addEventListener("loadeddata", predictWebcam);
    });
};

createHandLandmarker();

let lastVideoTime = -1;

async function predictWebcam() {
    canvasElement.width = video.videoWidth;
    canvasElement.height = video.videoHeight;

    if (runningMode === "IMAGE") {
        runningMode = "VIDEO";
        await handLandmarker.setOptions({ runningMode: "VIDEO" });
    }

    if (lastVideoTime !== video.currentTime) {
        lastVideoTime = video.currentTime;
        const startTimeMs = performance.now();
        const results = await handLandmarker.detectForVideo(video, startTimeMs);

        canvasCtx.clearRect(0, 0, canvasElement.width, canvasElement.height);

        if (results.landmarks) {
            for (const landmarks of results.landmarks) {
                // Get the index finger tip landmark
                const indexFingerTip = landmarks[14]; // Landmark index for the tip of the index finger
                drawRing(indexFingerTip);
            }
        }
    }

    if (webcamRunning) {
        window.requestAnimationFrame(predictWebcam);
    }
}

function drawRing(landmark) {
    const { x, y } = landmark;
    const canvasWidth = canvasElement.width;
    const canvasHeight = canvasElement.height;

    // Convert normalized coordinates to canvas coordinates
    const canvasX = (1 - x) * canvasWidth; // Flip the X coordinate
    const canvasY = y * canvasHeight;

    // Flip the ring image horizontally by scaling the context
    const ringSize = 40; // Adjust the ring size to your liking

    // Save the current context state before transforming
    canvasCtx.save();

    // Move the context to where the ring will be drawn
    canvasCtx.translate(canvasX, canvasY);

    // Flip the context horizontally by scaling it with a negative x-axis
    canvasCtx.scale(-1, 1);

    // Draw the ring image at the translated position, adjusted for size
    canvasCtx.drawImage(ringImage, -ringSize / 2, -ringSize / 2, ringSize, ringSize);

    // Restore the context to its original state
    canvasCtx.restore();
}