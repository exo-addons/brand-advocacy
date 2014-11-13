/**
 * Created by exoplatform on 13/10/14.
 */
(function($) {

  var _ftStepContainer;
  var _isValidTweetMsg;
  var bradObj = {};

  var _checkFtForm = function(missionId, propositionId ){
    var res = true;
    if(null == missionId || 0 == missionId || typeof missionId == "undefined")
      res = false;
    else if(null == propositionId || 0 == propositionId || typeof propositionId == "undefined")
      res = false;

    if(!res)
      _ftStepContainer.html(" something went wrong, cannot identify current mission");
    return res;
  };
  var _loadTerminateView = function(){
    $(".jz").jzAjax("JuZFrontEndApplication.loadTerminateView()",{
      success: function(data){
        if(typeof data == "string" && data != "nok"){
          _ftStepContainer.html(data);
          _addOptionCountries();
        }
        else
          _ftStepContainer.html("something went wrong, please retry it later");
      }
    });
  };
  var _loadStartView = function () {
    $(".jz").jzLoad("JuZFrontEndApplication.loadStartView()",
    function(data){
      $("#brandadvocacy-ft").html(data);
    });
  };

  var _addEventToBtnDiscovery = function(){
    $(document).on('click.juzbrad.ft.discovery.view','#brad-ft-discovery',function(){
      var jDiscovery = $(this);
      jDiscovery.jzAjax("JuZFrontEndApplication.loadStepContainerView()", {
        success: function (data) {
          $("#brad-ft-container").html(data);
          _ftStepContainer = $(".brad-container-step-container");
          $(".jz").jzAjax("JuZFrontEndApplication.initView()", {
            success: function (data) {
              _ftStepContainer.html(data);
              _addOptionCountries();
            }
          });
        }
      });
    });
  };

  var _addEventToBtnClose = function(){
    $(document).on('click.juzbrad.ft.index.view','.btn-brad-close',function(){
      var jClose = $(this);
      jClose.jzAjax("JuZFrontEndApplication.loadIndexView()",{
        success: function(data){
          $("#brad-ft-container").html(data);
        }
      });
    });
  };

  var _addEventToBtnStart = function(){
    $(document).on('click.juzbrad.ft.start.view','.btn-start',function(){
      var jStart = $(this);
      jStart.jzAjax("JuZFrontEndApplication.loadProcessView()",{
        success: function(data){
          if(typeof data == "string" && data != "nok")
            _ftStepContainer.html(data);
          else
            _ftStepContainer.html("something went wrong !!!, please try later");
        }
      });
    });
  };

  var _addEventToBtnDone = function(){
    $(document).on('click.juzbrad.ft.done.view','.btn-brad-done',function(){
      var missionId = $(".missionId").val();
      var propositionId = $(".propositionId").val();
      if(-_checkFtForm(missionId,propositionId)) {
        _loadTerminateView();
      }
    });
  };

  var _addEventToBtnTerminate = function(){
    $(document).on('click.juzbrad.ft.terminate.view','.btn-brad-terminate',function(){
      var jTerminate = $(this);
      var url = $("#brad-participant-url-submitted").val();
      var fname = $("#brad-participant-fname").val();
      var lname = $("#brad-participant-lname").val();
      var address = $("#brad-participant-address").val();
      var city = $("#brad-participant-city").val();
      var phone = $("#brad-participant-phone").val();
      var country = $("#brad-participant-country").val();
      var size = $("#brad-participant-size").val();
      jTerminate.jzAjax("JuZFrontEndApplication.loadThankyouView()",{
        data:{'url':url,fname:fname,lname:lname,address:address,city:city,phone:phone,country:country,size:size},
        success: function(data){
          if(typeof data == "string" && data != "nok"){
            _ftStepContainer.html(data);
            _tweetController();
          }
          else
            _ftStepContainer.html("something went wrong, please reload this page");
        }
      });
    });
  };

  var _addOptionCountries = function() {
    if($('#brad-participant-country').length > 0){
      var strOptions = '<option value="">Country</option>';
      $.getJSON("/brand-advocacy-webapp/resources/countries.json", function(data){
        $.each(data, function(i,v){
          strOptions +='<option value="'+v.code+'">'+v.name+'</option>';
        });
        $('#brad-participant-country').append(strOptions);
      });
    }
  };

  var _tweetController = function(){
    $("#txt-brad-tweet").keypress(function(){
      var legth = $(this).val().length;
      if(legth > 108) {
        _tweetErrorMessageController(true);
      } else {
        _tweetErrorMessageController(false);
      }
    });
  };
  var _tweetErrorMessageController = function(b){
    if(b){
      $(".brad-errors").show("slow");
      $(".btn-brad-tweet").attr("href", "#");
      $(".btn-brad-tweet").attr("disabled", "disabled");
      _isValidTweetMsg = false;
    }else{
      $(".brad-errors").hide("fast");
      $(".btn-brad-tweet").removeAttr("disabled");
      _isValidTweetMsg = true;
    }
  };

  var _addEvent2LinkTweet = function(){
    $(document).on('click.juzBrad.ft.tweetit','a.btn-brad-tweet',function(){
      if(!_isValidTweetMsg || $("#txt-brad-tweet").val().length > 108){
        _tweetErrorMessageController(true);
        return;
      }
      var tweetMessage = '';
      var defaultTweetMsg = "I just downloaded @eXoPlatform, Check it out!";
      var textareaMessage = $("#txt-brad-tweet").val();
      var strTweet = "http://twitter.com/share?url=http://bit.ly/1jBHRA6&hashtags=oss,esn&text=";

      if(textareaMessage.length > 0)  {
        tweetMessage = strTweet + encodeURI(textareaMessage);
      } else {
        tweetMessage = strTweet + encodeURI(defaultTweetMsg);
      }

      $(this).attr("href", tweetMessage);

      var width  = 575,
        height = 400,
        left   = ($(window).width()  - width)  / 2,
        top    = ($(window).height() - height) / 2,
        url    = this.href,
        opts   = 'status=1' +
          ',width='  + width  +
          ',height=' + height +
          ',top='    + top    +
          ',left='   + left;
      window.open(url, 'twitter', opts);

      return false;
    });
  };
  bradObj.init = function(){
    _isValidTweetMsg = true;
    _addEventToBtnDiscovery();
    _addEventToBtnStart();
    _addEventToBtnDone();
    _addEventToBtnTerminate();
    _addEventToBtnClose();
    _addEvent2LinkTweet();
  }
  return bradObj;
})($);