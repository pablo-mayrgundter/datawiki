<%
  final String geepServer = request.getParameter("geep.server");
%>
<script type="text/javascript">
var GEE_SERVER_URL = window.location.protocol + '//'
                     + '<%= geepServer %>/';
var GEE_POLYGON_DISPLAY_TIME = 1500;

function InitMap() {
  // Options passed to the v3 map object.
  var mapOpts = {
      zoom: 2,
      center: new google.maps.LatLng(0, 0),
      navigationControl: true,
      mapTypeControl: false,
      scaleControl: true,
  };
  geeInitMap(mapOpts);
}
</script> 
<!-- Load the required Javascript files.
     The local Maps API files fusionmaps_local.js and fusionmapobj.js.
     Utilities for this example UI: fusion_utils.js and search_tabs.js
     The main routine: geeInit() is found in fusion_maps.js which 
     defines the example UI and behaviors.
  --> 
<script type="text/javascript" src="/js/jQuery.js"></script> 
<script type="text/javascript" src="/local/js/310/params.js"></script> 
<script type="text/javascript" src="/local/js/310/bootstrap_loader.js"></script> 
<script type="text/javascript" src="/local/js/310/fusion_map_obj_v3.js"></script> 
<script type="text/javascript" src="/local/js/fusion_utils.js"></script> 
<script type="text/javascript" src="/local/js/search_tabs.js"></script> 
<script type="text/javascript" src="/local/js/fusion_maps_v3.js"></script> 
