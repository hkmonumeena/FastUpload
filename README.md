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
	      implementation 'com.github.hkmonumeena:FastUpload:1.0.0'

	}


Step 4. Start Image picker. 

 	buttonSingle.setOnClickListener { QuickImagePicker.singleImageDialog(this,132) }
	
    	buttonMultiple.setOnClickListener { QuickImagePicker.multiImageDialog(this,133) }

Step 5. Get result in onActivityResult.
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
       if (requestCode == 132 && resultCode == Activity.RESULT_OK) {
            val myImage = QuickImagePicker.getCompressImg(data?.data,data?.extras,"folderName",this)
            val myImageOriginalSize = QuickImagePicker.getWithoutCompressImage(this,data?.data!!)

        }
        if (requestCode == 133 && resultCode == Activity.RESULT_OK) {

            val myImagesList = QuickImagePicker.getListOfFiles(data?.clipData,this,5)

        }
    } 

Features:

1- Get image in just two lines of code.

2- fast and time saving.

3- Single or multiple image option.

4- we can get Origional image size

5- we can compress image size

6- we can set image limit

