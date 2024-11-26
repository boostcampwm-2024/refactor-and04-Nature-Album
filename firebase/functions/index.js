/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const {onRequest} = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

exports.helloWorld = onRequest((request, response) => {
  logger.info("Hello logs!", {structuredData: true});
  response.send("Hello from Firebase! And04 Nature Album");
});

const {onDocumentCreated} = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");


admin.initializeApp();


// Firestore 트리거: 새 문서 생성 이벤트
exports.notifyOnDocumentCreate = onDocumentCreated("TEST/{documentId}", async (event) => {
    const snapshot = event.data;
    
    // 문서 데이터 가져오기
    if (!snapshot) {
      console.log("No data found in the document.");
      return;
    }
  
    const data = snapshot.data();
    console.log("New document created in TEST collection:", data);
  
    // 예: FCM 메시지 전송
    // const message = {
    //   notification: {
    //     title: "Firestore 새 문서",
    //     body: `새 데이터: ${data.message || "내용 없음"}`,
    //   },
    //   topic: "test-topic",
    // };
  
    const message = {
        notification: {
            title: "Firestore 새 문서",
            body: `새 데이터: ${data.message || "내용 없음"}`,
        },
        data: {
            additionalInfo: "추가 데이터",
        },
        topic: "test-topic",
    };
    
    try {
      const response = await admin.messaging().send(message);
      console.log("FCM 메시지 전송 성공:", response);
    } catch (error) {
      console.error("FCM 메시지 전송 실패:", error);
    }
  });



exports.onFriendRequestCreated = onDocumentCreated(
  "USER/{uid}/FRIEND_REQUESTS/{requestUid}",
  async (event) => {
    const snapshot = event.data; // 새로 생성된 문서 데이터
    const uid = event.params.uid; // 요청을 받는 사용자 ID
    const requestUid = event.params.requestUid; // 요청 문서 ID

    // 데이터 확인
    if (!snapshot) {
      console.log("No snapshot available.");
      return;
    }

    const newRequest = snapshot.data();

    // 요청 상태가 RECEIVED인지 확인
    if (newRequest?.status === "RECEIVED") {
      console.log(`Friend request RECEIVED by user: ${uid}`);

      try {
        // Firestore에서 요청 받은 사용자(Friend 요청 대상)의 FCM 토큰 가져오기
        const userDoc = await admin.firestore().collection("USER").doc(uid).get();
        const userData = userDoc.data();
        const fcmToken = userData?.fcmToken;

        if (!fcmToken) {
          console.log(`No FCM token found for user: ${uid}`);
          return;
        }

        // Firestore에서 요청 보낸 사용자 정보 가져오기
        const sender = newRequest.user; // 요청 보낸 사용자 정보
        const senderDisplayName = sender?.displayName || "알 수 없는 사용자";

        // FCM 알림 메시지 생성
        const message = {
          token: fcmToken,
          notification: {
            title: "새로운 친구 요청",
            body: `${senderDisplayName}님으로부터 친구 요청이 도착했습니다.`,
          },
          data: {
            senderUid: sender.uid,
            senderDisplayName: sender.displayName,
            requestUid: requestUid,
          },
        };

        // FCM 메시지 전송
        await admin.messaging().send(message);
        console.log(`Notification sent to user: ${uid}`);
      } catch (error) {
        console.error("Error sending notification:", error);
      }
    } else {
      console.log("Friend request not in RECEIVED state, skipping notification.");
    }
  }
);
