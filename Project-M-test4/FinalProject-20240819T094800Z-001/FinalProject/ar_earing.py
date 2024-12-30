import cv2
import mediapipe as mp
import numpy as np
from PIL import Image

# Initialize MediaPipe Face Mesh model
mp_face_mesh = mp.solutions.face_mesh
face_mesh = mp_face_mesh.FaceMesh()
mp_drawing = mp.solutions.drawing_utils

# Load your necklace PNG image
necklace_image = Image.open('image copy.png').convert("RGBA")

# Start capturing video from the webcam
cap = cv2.VideoCapture(0)

while cap.isOpened():
    ret, frame = cap.read()
    if not ret:
        break
    
    # Convert the frame to RGB
    image = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    
    # Process the image to find face landmarks
    results = face_mesh.process(image)
    
    # If face landmarks are found
    if results.multi_face_landmarks:
        for face_landmarks in results.multi_face_landmarks:
            # Get the landmarks for the chin and neck region
            chin_landmark = face_landmarks.landmark[152]  # Landmark 152 is near the chin
            h, w, _ = frame.shape
            
            # Calculate the coordinates for the necklace position
            x_necklace = int(chin_landmark.x * w) - 100  # Adjust for necklace position
            y_necklace = int(chin_landmark.y * h) + 50   # Adjust for necklace position
            
            # Resize the necklace image to fit the neck area
            necklace_resized = necklace_image.resize((200, 100))  # Adjust the size as needed
            
            # Convert the necklace image to an array (BGRA format for OpenCV)
            necklace_array = np.array(necklace_resized)
            
            # Separate the color and alpha channels
            necklace_bgr = necklace_array[:, :, :3]  # Color channels (BGR)
            necklace_alpha = necklace_array[:, :, 3]  # Alpha channel
            
            # Define the region of interest (ROI) on the frame for the necklace
            roi = frame[y_necklace:y_necklace+necklace_array.shape[0], x_necklace:x_necklace+necklace_array.shape[1]]

            # Use the alpha channel as a mask to blend the necklace with the background
            for c in range(0, 3):
                roi[:, :, c] = roi[:, :, c] * (1 - necklace_alpha / 255.0) + necklace_bgr[:, :, c] * (necklace_alpha / 255.0)

            # Place the blended ROI back onto the frame
            frame[y_necklace:y_necklace+necklace_array.shape[0], x_necklace:x_necklace+necklace_array.shape[1]] = roi
    
    # Display the result
    cv2.imshow('AR Necklace Try-On', frame)

    # Break the loop on 'q' key press
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# Release the camera and close all windows
cap.release()
cv2.destroyAllWindows()
