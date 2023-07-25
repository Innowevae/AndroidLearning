package com.innoweavebiocare.matsya

import android.content.Context
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify

class Cognito(context: Context) {
    init {
        try {
            // Add this line, to include the Auth plugin.
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(context)
            Log.i("MyAmplifyApp", "Initialized Amplify")
        } catch (error: AmplifyException) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error)
        }
    }
    /*
    ToDo: Handle all Code like Sign-in Sign-Out from this class
    Invole URL: https://rmnd0an6fc.execute-api.us-east-1.amazonaws.com/dev
    */
}