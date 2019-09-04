# SecurityApplication

Aim:To callback MainActivity on Completion of FaceBookLogin.setUser() method:
Problem : Unable/(Don't know) how to callback
Working:
On calling OnSuccess method for registration of FaceBookLogin at line 101 of MainActivity,
I'm calling handlemethod.. of FaceBookLoginIn.java class to authenticate with firebase and then setting the FirebaseUser
i.e user currentUser using setUser() method.
After updating user, I need to go back to the calling activity, but can't understand how to do.
Below in FaceBookLoginIn java class you would see an interface. That is what i am currently implementing to callback activity,
but not done yet.
