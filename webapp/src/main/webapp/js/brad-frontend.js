/**
 * Created by exoplatform on 13/10/14.
 */
(function($) {

  var _ftStepContainer;
  var _isValidTweetMsg;
  var _msgError;
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
  var _validateUrlFormat = function(uri) {
    var urlPattern = /(http|ftp|https):\/\/[\w-]+(\.[\w-]+)+([\w.,@?^=%&amp;:\/~+#-]*[\w@?^=%&amp;\/~+#-])?/;

    if (urlPattern.test(uri)) {
      // Successful match
      return true;
    } else {
      return false;
      // Match attempt failed
    }
  };
  var _displayLoading = function(){
    _ftStepContainer.html('We are searching a mission for you ...');
  };
  var _displayProcessing = function(){
    _ftStepContainer.html('Processing ...');
  };
  var _validateTerminateForm = function(){
    _msgError = "";
    var urlDOM = $("#brad_participant_url_submitted");
    var fNameDOM = $("#brad-participant-fname");
    var lNameDOM = $("#brad-participant-lname");
    var adrsDOM  =  $("#brad-participant-address");
    var phoneDOM = $("#brad-participant-phone");
    var cityDOM = $("#brad-participant-city");
    var errorClass = 'error';

    var urlParent = urlDOM.closest('.control-group');
    urlParent.removeClass(errorClass);
    var fNameParent = fNameDOM.closest('.control-group');
    fNameParent.removeClass(errorClass);
    var lNameParent = lNameDOM.closest('.control-group');
    lNameParent.removeClass(errorClass);
    var adrsParent = adrsDOM.closest('.control-group');
    adrsParent.removeClass(errorClass);
    var phoneParent = phoneDOM.closest('.control-group')
    phoneParent.removeClass(errorClass);
    var cityParent = cityDOM.closest('.control-group')
    cityParent.removeClass(errorClass);

    if(urlDOM.val().length < 1 || !_validateUrlFormat(urlDOM.val())) {
      _msgError += "Please fill-in the correct url\n";
      urlParent.addClass(errorClass);
    }
    if(fNameDOM.val().length < 1) {
      _msgError += "Please fill-in your first name\n";
      fNameParent.addClass(errorClass);
    }
    if(lNameDOM.val().length < 1) {
      _msgError += "Please fill-in your last name\n";
      lNameParent.addClass(errorClass);
    }
    if(adrsDOM.val().length < 1 ) {
      _msgError += "Please fill-in your address\n";
      adrsParent.addClass(errorClass);
    }
    if(cityDOM.val().length < 1) {
      _msgError += "Please fill-in your city\n";
      cityParent.addClass(errorClass);
    }
    if(!_validatePhoneNumber(phoneDOM.val())) {
      _msgError += "Please fill-in correct the phone\n";
      phoneParent.addClass(errorClass);
    }
    if($("#brad-participant-country").val().length < 1) {
      _msgError += "Please select your country\n";
    }
    return _msgError;
  };
  var _loadDiscoveryView = function(){
    $(".jz").jzAjax("JuZFrontEndApplication.loadDiscoveryView()",{
      success: function(data){
        if(typeof data == "string" && data != "nok"){
          $("#brad-ft-container").html(data);
          $(window).scroll(function (event) {
            var scroll = $(window).scrollTop();
            if(scroll > 45) {
              $(".tweetPopup").addClass('scroll-down');
            } else {
              $(".tweetPopup").removeClass('scroll-down');
            }
          });
        }
        else
          $("#brad-ft-container").html("something went wrong, please retry it later");
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
//          $("#brad-ft-container").fadeIn('1000');
          $("#brad-ft-container").html(data);
          _ftStepContainer = $(".brad-container-step-container");
          _loadStartView();
        }
      });
    });
  };

  var _addEventToBtnClose = function(){
    $(document).on('click.juzbrad.ft.index.view','.btn-brad-close',function(){
      $(".tweetPopup").removeClass('scroll-down');
      _loadDiscoveryView();
    });
  };

  var _addEventToBtnStart = function(){
    $(document).on('click.juzbrad.ft.start.view','.btn-brad-start',function(e){
      _displayProcessing();
      $('.jz').jzAjax("JuZFrontEndApplication.loadProcessView()",{
        success: function(data){
          if(typeof data == "string" && data != "nok"){
            _ftStepContainer.html(data);
          }
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
      if(_validateTerminateForm().length == 0){
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
              _addFocusEvent2Input();
              _sendNotifNewMissionParticipant();
            }
            else{
              alert("something went wrong, please reload this page");
              return;
            }
          }
        });
      }else{
        alert(_msgError);
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
      var defaultTweetMsg = "I just won a free t-shirt from @eXoPlatform on ";
      var textareaMessage = $("#txt-brad-tweet").val();
      var strTweet = "http://twitter.com/share?url=http://community.exoplatform.com&hashtags=ILOVEMYESN&text=";

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
  var _addFocusEvent2Input = function(){
    $("#txt-brad-tweet").focus(function() { $(this).select(); } );
  };
  bradObj.init = function(){
    _isValidTweetMsg = true;
    _loadDiscoveryView();
    _addEventToBtnDiscovery();
    _addEventToBtnStart();
    _addEventToBtnDone();
    _addEventToBtnTerminate();
    _addEventToBtnClose();
    _addEvent2LinkTweet();
  }
  return bradObj;
})($);