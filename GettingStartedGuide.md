# Get #

See the [Source](http://code.google.com/p/datawiki/source/checkout) page.

# Build #

```
cd <the code directory>
export APP_ENGINE_HOME=<path to your appengine-java-sdk>
ant
```

# Run #

```
ant server
```


# Develop #

CLI building is easy:

```
ant classpath
"... export CLASSPATH=..."
<copy and paste that back in to set your classpath>
cd wiki
javac *.java
```

Then [Request a Code Review](http://code.google.com/p/datawiki/issues/entry?show=review&former=sourcelist) for submission.