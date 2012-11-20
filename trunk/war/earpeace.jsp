<!DOCTYPE html> 
<html lang="en"> 
  <head> 
    <meta charset=utf-8 /> 
    <meta name=viewport content="width=320" /> 
    <title>EarPeace</title> 
    <style type="text/css"> 
      body,td,tr,input{
      margin: 0;
      padding: 0;
      font-family: "Lucida Grande",Helvetica,Arial;
      font-size: 12px;
      }
    </style> 
  </head> 
  <body> 
    <img id=header_image> 
    
    <form action="http://reliefhub.appspot.com/wiki/documents?format=Ear_Peace" method="POST" enctype="multipart/form-data"> 
    <table cellpadding=5 cellspacing=5> 
    <tr><td>Sound Level (dB):<td><input name="sound_level" size=5><td>(e.g. 76)<br/> 
    <tr><td>Location Name:<td><input name="location_name" size=30><td>(e.g. Madison Square Garden)<br/> 
    <tr><td>Your Name:<td><input name="submitter_name" size=30><br/> 
    <tr><td><td><input type="Submit"> 
    </table> 
    <input name="date" id=date type=hidden> 
    <input name="latitude" id=latitude type=hidden> 
    <input name="longitude" id=longitude type=hidden> 
    <input name="location_radius" id=location_radius type=hidden> 
    <input name="format" value="Ear_Peace" type="hidden"> 
    </form> 
 
 
    <script> 
 
    if (navigator.userAgent.match(/iPhone|iPod|Android/i)) {
      document.getElementById('header_image').src = "earpeace_iphone.png";
    } else {
      document.getElementById('header_image').src = "earpeace.png";
    }
 
    function success(position) {
      c = position.coords;
      document.getElementById('latitude').value = c.latitude;
      document.getElementById('longitude').value = c.longitude;
      document.getElementById('location_radius').value = c.accuracy;
      d = new Date();
      minutes = d.getMinutes();
      if (minutes < 10) {
        minutes = "0" + minutes;
      }
      document.getElementById('date').value =
                    (d.getMonth() + 1) + "/" + d.getDate() + "/" + d.getFullYear() + 
                    " " + d.getHours() + ":" + minutes;
    }
                  
    function my_print(s) {
      document.getElementById(fred).innerHTML += s;
    }
                  
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(success, my_print);
    } else {
      my_print('not supported');
    }
</script> 
