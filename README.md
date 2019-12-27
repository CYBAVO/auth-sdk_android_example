# CYBABO Auth SDK for Android - Sample

Sample app for integrating Cybavo Authenticator SDK, https://www.cybavo.com/

![image](https://github.com/CYBAVO/auth-sdk_android_example/raw/master/README/sc_security.png)
![image](https://github.com/CYBAVO/auth-sdk_android_example/raw/master/README/sc_main.png)
![image](https://github.com/CYBAVO/auth-sdk_android_example/raw/master/README/sc_pin.png)

## Features

- Pair/unpair device with service
- Fetch two-factor authentication actions from backend
- Respond to two-factor anthentication actions, with or without PIN code
- Receive push notifications for two-factor authentication

## Setup

1. Edit `local.properties` to config Maven repository URL / credentials provided by CYBAVO

   ```
   cybavo.maven.url=$MAVEN_REPO_URL
   cybavo.maven.username=$MAVEN_REPO_USRENAME
   cybavo.maven.password=$MAVEN_REPO_PASSWORD
   ```

2. Edit `values/config.xml` ➜ `default_endpoint` to point to your Wallet Service endpoont
3. Register your app on CYBAVO AUTH MANAGEMENT system web > Service > App Management, input `package name` and `Signature keystore SHA1 fingerprint`, follow the instruction to retrieve an `API Code`.
4. Edit `values/config.xml` ➜ `default_api_code` to fill in yout `API Code`
5. To enable push notification feature, setup project to integrate [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging) (FCM) service, download `google-services.json` to `/app` folder. Refer to [official document](https://firebase.google.com/docs/cloud-messaging/android/client) for details

## CYBAVO

A group of cybersecurity experts making crypto-currency wallet secure and usable for your daily business operation.

We provide VAULT, wallet, ledger service for cryptocurrency. Trusted by many exchanges and stable-coin ico teams, please feel free to contact us when your company or business need any help in cryptocurrency operation.