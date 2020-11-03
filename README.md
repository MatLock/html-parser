### HTML Parser 

How to Run it.

 * Clone this repo
 * execute ```mvn clean install``` to download related dependencies
 * go to *target* folder
 * execute  ```java -jar html-parser-1.0.0-SNAPSHOT.jar <componentid> <absolutePathToHtmlFileOrigin> <absolutePathToHtmlTarget>```

Example

``` java -jar target/html-parser-1.0.0.jar "#make-everything-ok-button" src/test/resources/origin.html src/test/resources/sample-evil-gemini.html``` 
``` java -jar html-parser-1.0.0.jar "#make-everything-ok-button" myPath/sample-0-origin.html myPath/sample-1-evil-gemini.html```
