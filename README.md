# Transcript-System
This is one of my personal projects which I worked on during my final year in my Undergraduate degree when I was preparing to travel Abroad to pursue my Master’s degree.
One of the requirements while applying to foreign universities is a Transcript. A Transcript is a document which represents your overall academic progress in a particular format which helps foreign universities to evaluate the student’s profile. 
The transcript must be thoroughly verified and authenticated by the University to provide its validity.
To do that there are multiple stages involved, such as first preparing the transcript and submitting it to your college’s exam cell, which then gets verified and signed by the administrator which later you can collect and submit. This process in my college was carried out by using a penpaper method (A diary) to store student records who have submitted transcripts for attestation. So, I thought a good project would be to automate the process and create a windows application for it which also notifies the user through email and helps students be up to date on the progress of this process.

## Technology Stack:
- JavaFX – For UI Design
- Sqlite- For database
- Java for core logic
- Maven for dependency management
- JUnit for testing

### Features:
1. Maintains student records efficiently to know which all students have applied for
attestation of their transcripts.
2. Has different stages such as received, ready and collected to track the progress of the
overall progress
3. On each stage, the user gets notified through an email.
4. At Each stage you can also know the timestamp at which each process took place

### Add New Record with field Validations
<img width="659" alt="image" src="https://user-images.githubusercontent.com/36131683/158054594-e49b0548-89d7-4d81-97f7-a241a5390368.png">
<img width="655" alt="image" src="https://user-images.githubusercontent.com/36131683/158054606-77c27951-d10c-429f-b724-551b0aca3fc2.png">
<img width="652" alt="image" src="https://user-images.githubusercontent.com/36131683/158054610-2c96f536-c568-46f5-8f62-ac08ae9e1d59.png">

- Checks for valid and unique names, mobile numbers, and email addresses.
- Cancel button resets all the fields to blank.

### Student List View
<img width="659" alt="image" src="https://user-images.githubusercontent.com/36131683/158054684-8e5b5bd1-150b-4241-bb5f-e559fdde7803.png">

- 1: Indicates complete state, 0: To be completed.
- We can edit any field by double clicking it and making changes to it.
- We can also delete any student record; it also supports selecting multiple records to delete at once

### Different Stages of the Process (Received, Ready,and Collected)
<img width="658" alt="image" src="https://user-images.githubusercontent.com/36131683/158054703-3249f900-61e8-4211-9e46-a969149fd737.png">
<img width="654" alt="image" src="https://user-images.githubusercontent.com/36131683/158054710-52dbc043-d99e-4838-844e-318b605ff879.png">
<img width="659" alt="image" src="https://user-images.githubusercontent.com/36131683/158054719-213c6e16-f8cf-4d37-813c-203aa26347ec.png">

- At Each stage you can click the button below to move it to the next stage.
- Also supports multi select to move multiple students at once.

### Email Message
<img width="667" alt="image" src="https://user-images.githubusercontent.com/36131683/158054754-099fd9a1-09ec-4c0a-8abb-16793a3edb34.png">

- The subject and the body are unique for each stage.
- Custom personal greeting

## The End
