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

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
const functions = require("firebase-functions");
const admin = require("firebase-admin");

// Firebase Admin 초기화
admin.initializeApp();

exports.onFriendRequestCreated = functions.firestore
    .document("USERS/{uid}/FRIEND_REQUESTS/{requestUid}")
    .onCreate(async (snapshot, context) => {
        const newRequest = snapshot.data(); // 새로 생성된 문서 데이터
        const uid = context.params.uid; // 요청을 받는 사용자 ID
        const requestUid = context.params.requestUid; // 요청 문서 ID

        // 요청 상태가 RECEIVED인 경우만 처리
        if (newRequest.status === "RECEIVED") {
            console.log(`Friend request RECEIVED by user: ${uid}`);

            try {
                // Firestore에서 요청 보낸 사용자 정보 가져오기
                const sender = newRequest.user; // 요청 보낸 사용자 정보 (FirestoreUser 형태)
                const userDoc = await admin.firestore().collection("USERS").doc(uid).get();
                const fcmToken = userDoc.data()?.fcmToken;

                if (!fcmToken) {
                    console.log(`No FCM token found for user: ${uid}`);
                    return;
                }

                // FCM 알림 메시지 생성
                const message = {
                    token: fcmToken,
                    notification: {
                        title: "새로운 친구 요청",
                        body: `${sender.displayName}님으로부터 친구 요청이 도착했습니다.`,
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
    });

