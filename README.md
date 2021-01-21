# Quick Image Picker

Usage:

Step 1. Add it in your root build.gradle at the end of repositories:
            
      allprojects {
              repositories {
                ...
                maven { url 'https://jitpack.io' }
              }
            }


Step 2. Add the dependency

	dependencies {
	            implementation 'com.github.hkmonumeena:TwoM:1.0.1'

	}


POST (Kotlin). For POST method. 

 	  // for post method
        TwoM.post("url") // add your url
        TwoM.headerParameter("key","value") // add your header parameter if any else just remove the line
        TwoM.bodyParameter("key","value") // add your body parameter if any else just remove the line
        TwoM.PostExecute().get { result ->
            
            Log.e("Result", "Result is here", ) // TwoM.PostExecute().get{ result -> }  here your response will be show  thats it
        }
POST (Java). For POST method.


	TwoM.INSTANCE.post("url");
        TwoM.INSTANCE.bodyParameter("category_id", "1");
        TwoM.INSTANCE.bodyParameter("subcategory_id", "1");
        TwoM.PostExecute postExecute = new TwoM.PostExecute();
        postExecute.get(new Function1<String, Unit>() {
            @Override
            public Unit invoke(final String result) {
                Log.e("Result", "Result is here"+result ); // TwoM.PostExecute().get{ result -> }  here your response will be show  thats it
                
                return null;
            }
        });
	
	

Features:

1- Easy to use.

2- fast and time saving.

3- Get server response in very less code 

