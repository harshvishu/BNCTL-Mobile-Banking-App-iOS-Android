#  Steps to create build variants (iOS)


Now we are creating dev & local host  .*xcconfig* file.

1) open *File* -> *New* -> *File* and select *Configuration Settings File*, name its *dev.xcconfig*.

2) After creating config file, We will add BASE_URL, APP_NAME & APP_BUNDLE_ID etc. So it looks like below code.
   `BASE_URL = http:/$()/192.168.132.220:8005/api/`
   `APP_NAME = Allianz Dev`
   `APP_BUNDLE_ID = com.softwaregroup.banking.allianz`

3) Now we have create build configuration. Select *Project* -> *Info* -> *Configurations* , Create a new config and name its as dev and change the configuration settings to dev (*dev.xcconfig* Which is already created, it will be in the list).

4)  We have to create Scheme.  Select *Product* -> *Scheme* -> *Manage Scheme*, Add a new scheme and name its as dev along with app name. For example *Allinaz (iOS) Dev*. Then select *Edit*, In *Run* > *Info* section change the *Build Configuration* name to dev. This have to be repeated for *Test* > *Info*, *Profile* > *Info*, *Analize* and *Archive* 

5) Next step we have to access the configuration values which we added in *dev.xcconfig* file. First we will add APP_BUNDLE_ID. Select  *Project* -> *Targets* -> *Allianz  (iOS)*  -> *Packaging* -> *Product Build Identifier* -> *dev*, Then change the value to *$(APP_BUNDLE_ID)*.
    Next step  *Project* -> *Targets* -> *Allianz  (iOS)*  -> *Info* -> *Custom iOS Target Properties* -> *Bundle Name* Then change the value to *$(APP_NAME)*, Now and a new value in same section(*Custom iOS Target Properties*)  BASE_URL and the value is *$(BASE_URL)*.

6) When can access url by using the following code.
  `let BASE_URL = Bundle.main.infoDictionary?[“BASE_URL”]  as! String`

Now we can select dev scheme to build the project. Follow the same step to create new scheme in future.
