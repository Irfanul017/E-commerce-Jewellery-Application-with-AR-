import cv2
import mediapipe as mp
import numpy as np
from PIL import Image

# Initialize MediaPipe Hand model
mp_hands = mp.solutions.hands
hands = mp_hands.Hands()
mp_drawing = mp.solutions.drawing_utils

# Load your ring PNG image
ring_image = Image.open('ring png.png').convert("RGBA")

# Start capturing video from the webcam
cap = cv2.VideoCapture(0)

while cap.isOpened():
    ret, frame = cap.read()
    if not ret:
        break
    
    # Convert the frame to RGB
    image = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    
    # Process the image to find hands
    results = hands.process(image)
    
    # If hand landmarks are found
    if results.multi_hand_landmarks:
        for hand_landmarks in results.multi_hand_landmarks:
            # Get the ring finger DIP (Distal Interphalangeal Joint) landmark
            ring_finger_dip = hand_landmarks.landmark[mp_hands.HandLandmark.RING_FINGER_DIP]
            h, w, _ = frame.shape
            
            # Calculate the coordinates with some offset to align the ring
            x = int(ring_finger_dip.x * w) - 25  # Shift right by 25 pixels (adjust as needed)
            y = int(ring_finger_dip.y * h) - 25  # Shift upward by 25 pixels (adjust as needed)
            
            # Resize the ring image to fit the finger
            ring_resized = ring_image.resize((50, 50))  # Adjust the size as needed
            
            # Convert the ring image to an array
            ring_array = np.array(ring_resized)
            
            # Define the region of interest (ROI) on the frame
            roi = frame[y:y+ring_array.shape[0], x:x+ring_array.shape[1]]

            # Extract the alpha channel (transparency)
            ring_alpha = ring_array[:, :, 3] / 255.0
            background_alpha = 1.0 - ring_alpha

            # Blend the ring image with the ROI
            for c in range(0, 3):
                roi[:, :, c] = (ring_alpha * ring_array[:, :, c] +
                                background_alpha * roi[:, :, c])

            # Place the blended ROI back onto the frame
            frame[y:y+ring_array.shape[0], x:x+ring_array.shape[1]] = roi
    
    # Display the result
    cv2.imshow('AR Ring Try-On', frame)

    # Break the loop on 'q' key press
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# Release the camera and close all windows
cap.release()
cv2.destroyAllWindows()
