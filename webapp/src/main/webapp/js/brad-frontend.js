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
  var _validatePhoneNumber = function(phone){
      intRegex = /[0-9 -()+]+$/;
    if((phone.length < 6) || (!intRegex.test(phone)))
    {
      return false;
    }
    return true;
  };
  var _displayLoading = function(){
    _ftStepContainer.html('We are searching a mission for you ...');
  };
  var _displayProcessing = function(){
    _ftStepContainer.html('Processing ...');
  };
  var _validateTerminateForm = function(){
    if($("#brad_participant_url_submitted").val().length < 1) {
      return false;
    } else if($("#brad-participant-fname").val().length < 1) {
      return false;
    } else if($("#brad-participant-lname").val().length < 1) {
      return false;
    } else if($("#brad-participant-address").val().length < 1 ) {
      return false;
    } else if($("#brad-participant-city").val().length < 1) {
      return false;
    } else if(!_validatePhoneNumber($("#brad-participant-phone").val())) {
      return false;
    } else if($("#brad-participant-country").val().length < 1) {
      return false;
    }
    return true;
  };
  var _loadTerminateView = function(){
    _displayProcessing();
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
    _displayLoading();
    $(".jz").jzAjax("JuZFrontEndApplication.loadStartView()",{
     success:function(data){
       _ftStepContainer.html(data);
     }
    });
  };
  // for old logic: load current mission for participant
  var _initView = function(){
    _displayLoading();
    $(".jz").jzAjax("JuZFrontEndApplication.initView()", {
      success: function (data) {
        _ftStepContainer.html(data);
        _addOptionCountries();
      }
    });
  };

  var _addEventToBtnDiscovery = function(){
    $(document).on('click.juzbrad.ft.discovery.view','#brad-ft-discovery',function(){
      var jDiscovery = $(this);
      jDiscovery.jzAjax("JuZFrontEndApplication.loadStepContainerView()", {
        success: function (data) {
          $("#brad-ft-container").html(data);
          _ftStepContainer = $(".brad-container-step-container");
          _loadStartView();
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
    $(document).on('click.juzbrad.ft.start.view','.btn-brad-start',function(e){
      _displayProcessing();
      $('.jz').jzAjax("JuZFrontEndApplication.loadProcessView()",{
        success: function(data){
          if(typeof data == "string" && data != "nok")
            _ftStepContainer.html(data);
          else
            _ftStepContainer.html("something went wrong !!!, please try later");
        }
      });
    });
  };
  var _sendNotifNewMissionParticipant = function(){
    $('.jz').jzAjax('JuZFrontEndApplication.sendNotifEmail()',{
      success:function(data){
        if(data == "ok"){
          _sendNotifAlmostMissionDoneEmail();
        }
      }
    });
  };
  var _sendNotifAlmostMissionDoneEmail = function(){
    $('.jz').jzAjax('JuZFrontEndApplication.sendNotifAlmostMissionDoneEmail()',{
      success:function(data){
      }
    });
  };
  var _addEventToBtnDone = function(){
    $(document).on('click.juzbrad.ft.done.view','.btn-brad-done',function(){
      var label = $(this).text();
      if(label == "Next"){
        var missionId = $(".missionId").val();
        var propositionId = $(".propositionId").val();
        if(_checkFtForm(missionId,propositionId)) {
          _loadTerminateView();
        }
      }else{
        var third_part_link = $(this).attr('data-url');
        $(this).text('Next');
        window.open(third_part_link, '_blank');
      }
    });
  };

  var _addEventToBtnTerminate = function(){
    $(document).on('click.juzbrad.ft.terminate.view','.btn-brad-terminate',function(){
      if(_validateTerminateForm()){
        var jTerminate = $(this);
        var url = $("#brad_participant_url_submitted").val();
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
              _sendNotifNewMissionParticipant();
            }
            else{
              alert("something went wrong, please reload this page");
              return;
            }
          }
        });
      }else{
        alert('Please fill all informations!')
      }
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