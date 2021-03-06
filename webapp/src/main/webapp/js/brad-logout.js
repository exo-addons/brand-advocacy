/**
 * Created by exoplatform on 13/10/14.
 */
(function($) {
  var _brandAdvLandingPageContainer;
  var _pickMissionSliderContainer;
  var _brandStepBackgroundContainer;
  var _brandLeftPanelContainer;
  var _brandAdvFtContainer;
  var _giveUpPopupDOM;
  var _ftStepContainerTemp;
  var _ftStepContainer;

  var _checkGiveUp;
  var _createNew;
  var _isValidTweetMsg;
  var _msgError;
  var _utm_campaign,_utm_content,_utm_term,_utm_medium,_utm_source;
  var _disableUTMCookie = false;
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
  var _displayNoMoreMission = function(){
    _ftStepContainer.html('We are preparing next mission, please come back later');
  };
  var _displayProcessError = function(){
    _ftStepContainer.html('Something went wrong,please try later');
  };

  var _appendStepCommon = function(content){
    _ftStepContainer.html('');
    content.appendTo('.brad-container-step-container');
  };
  var _removeStepCommon = function(step){
    var stepDOM = _ftStepContainerTemp.children('.'+step);
    if(stepDOM.length){
      stepDOM.remove();
    }
  };

  var _loadThankyouView = function(){
    $('.jz').jzAjax("JuZLogoutApplication.loadThankyouView()",{
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
  };

  var _reloadMission = function(){
    $(".tweetPopup").removeClass('scroll-down');
    _showPickMission(true);
    _createNew = true;
    _checkGiveUp = false;
  };

  var _addEvent2BtnClose = function(){
    $(document).on('click.juzbrad.ft.close.view','.btn-brad-close',function(){
      if(_checkGiveUp){
        _giveUpPopupDOM.show();
      }else{
        _reloadMission();
      }
    });
  };

  var _sendNotifNewMissionParticipant = function(){
    $('.jz').jzAjax('JuZLogoutApplication.sendNotifEmail()',{
      data:{disableUTMCookie:_disableUTMCookie,utm_content:_utm_content,utm_term:_utm_term,utm_campaign:_utm_campaign,utm_medium:_utm_medium,utm_source:_utm_source},
      success:function(data){
        if(data == "ok"){
          _sendNotifAlmostMissionDoneEmail();
        }
      }
    });
  };
  var _sendNotifAlmostMissionDoneEmail = function(){
    $('.jz').jzAjax('JuZLogoutApplication.sendNotifAlmostMissionDoneEmail()',{
      success:function(data){
      }
    });
  };


  var _tweetController = function(){
    _ftStepContainer.children('.brad-thankyou-step').find(".txt-brad-tweet").keypress(function(){
      var length = $(this).val().length;
      if(length > 108) {
        _tweetErrorMessageController(true);
      } else {
        _tweetErrorMessageController(false);
      }
    });
  };
  var _tweetErrorMessageController = function(b){
    var parent = _ftStepContainer.children('.brad-thankyou-step');
    if(b){
      parent.find(".brad-errors").show("slow");
      parent.find(".btn-brad-tweet").attr("href", "#");
      parent.find(".btn-brad-tweet").attr("disabled", "disabled");
      _isValidTweetMsg = false;
    }else{
      parent.find(".brad-errors").hide("fast");
      parent.find(".btn-brad-tweet").removeAttr("disabled");
      _isValidTweetMsg = true;
    }
  };

  var _addEvent2LinkTweet = function(){
    $(document).on('click.juzBrad.ft.tweetit','a.btn-brad-tweet',function(){
      var parent = $(this).parents('.step-2');
      var textareaMessageDOM = parent.find(".txt-brad-tweet");
      if(!_isValidTweetMsg || textareaMessageDOM.val().length > 108){
        _tweetErrorMessageController(true);
        return;
      }
      var tweetMessage = '';
      var defaultTweetMsg = "I just won a free t-shirt from @eXoPlatform on ";
      var strTweet = "http://twitter.com/share?url=http://community.exoplatform.com&hashtags=ILOVEMYESN&text=";

      if(textareaMessageDOM.val().length > 0)  {
        tweetMessage = strTweet + encodeURI(textareaMessageDOM.val());
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
    $('textarea').on('mouseup', function() {
      $(this).val('I just won a free t-shirt from @eXoPlatform on http://community.exoplatform.com #ILOVEMYESN');
      $(this).select();
    });
  };


  var _ctrlDown;
  var _ctrlKey;
  var _cKey;

  var _addEventClick2GetSuggestion = function(){
    $(document).on('click.juzBrad.Proposition.Click','.brad-proposition-suggestion',function(){
      var pDOM = $(this).children('div.box-view-value').children('p');
      var textareaDOM = $(this).children('textarea');
      textareaDOM.html(pDOM.text());
      pDOM.hide();
      textareaDOM.show();
      textareaDOM.select();
    });
  };
  var _addEventKeyDownCopy = function(){
    $(document).on('keydown.juzBrad.Proposition.Copy','body',function(e){
      if (_ctrlDown && e.keyCode == _cKey) {
        var quoteDOM = _ftStepContainer.children('.brad-process-step').children('.quote-popup');
//        quoteDOM.children('textarea').hide();
//        quoteDOM.children('p').show();
      }
    });
  };
  var _addEvent2DetectCopyAction = function(){

    $(document).keydown(function(e)
    {
      if (e.keyCode == _ctrlKey){ _ctrlDown = true; }
    }).keyup(function(e)
    {
      if (e.keyCode == _ctrlKey) _ctrlDown = false;
    });
  };



  var _loadSlices = function(){

  };
  var _showPickMission = function(b){
    if(!b){
      // need to hide slider container using css
      _checkGiveUp = true;
      $(_pickMissionSliderContainer).css('position','absolute');
      _brandAdvFtContainer.show();
    }else{
      _checkGiveUp = false;
      _brandAdvFtContainer.hide();
      $('.brad-btn-pick-mission').show();
      $(_pickMissionSliderContainer).removeAttr('style');
    }
  };
  var _initView = function(){
    _removeStepCommon('brad-process-step');
  };

  var _addEvent2BtnPickMission = function(){
    $ (document).on('click.juzBrad.pickmission','.brad-btn-pick-mission',function(){
      $('.brad-btn-pick-mission').hide();
      var stepsDOM = $('.bg-step step-1');
      if (!stepsDOM.hasClass('active')){
        stepsDOM.addClass('active');
      }
      if(_createNew){
        _processGenerateNewMission();
      }else{
        _loadMissionView();
        //_showPickMission(false);
      }

    });
  };

  var _processGenerateNewMission = function(){
    $('.jz').jzAjax('JuZLogoutApplication.generateNewMission()',{
      success:function(data){
        if(data == 'nok'){
          _displayNoMoreMission();
        }else if(data == 'ok'){
          _loadMissionView();
        }else{
          _ftStepContainer.html(data);
        }
        _showPickMission(false);
      }
    });
  };

  var _loadMissionView = function(){
    _displayLoading();
    $('.jz').jzAjax("JuZLogoutApplication.loadProcessView()",{
      success: function(data){
        if(data != "nok"){
          _switchStepCommon('step1',data);
          _showPickMission(false);
        }
      }
    });
  };

  var _switchStepCommon = function(stepBackground,stepContent){
    if(stepBackground != ''){

      stepBackground = stepBackground.match(/\d+/);
      if ($('.mission-'+stepBackground).length > 0){
        if(stepBackground == 1){
          $('.list-misstion li').each(function(i,v){
            $(v).removeClass('passed');
          });
          $('.step-1').addClass('active');
          $('.step-2').removeClass('active');

        }
        var stepDOM = $('.mission-'+stepBackground);
        if (!stepDOM.hasClass('passed'))
          stepDOM.addClass('passed');
      }else{
        $('.step-1').removeClass('active');
        $('.step-2').addClass('active');
      }

    }
    if(stepContent != '')
      _ftStepContainer.html(stepContent);
  };
  var _addEventToBtnGo = function(){
    $(document).on('click.juzbrad.ft.done.view','.btn-brad-go',function(){
      var parentElement = $(this).parents('.brad-process-step');
      var label = $(this).text();
      var missionId = $(this).attr("data-missionId");
      var propositionId = $(".propositionId").val();
      if(_checkFtForm(missionId,propositionId)) {
        _prepareView4TerminateStep();
        _urlSubmitted = "";
        var third_part_link = $(this).attr('data-url');
        parentElement.children('.title-step-1').hide();
        parentElement.children('.title-step-2').show();
        $(this).hide();
        $('.brad-complete-step').show();
        _switchStepCommon('step2','');
        window.open(third_part_link, '_blank');
      }else{
        _displayProcessError();
      }

    });
  };

  var _executeMission = function(){
    $('.jz').jzAjax("JuZLogoutApplication.executeMission()",{
      success: function(data){
        if(data == "ok"){
          _switchStepCommon('step2','');
          _prepareView4TerminateStep();
          $('.brad-complete-step').show();
        }
        else if(data == 'nok')
          _ftStepContainer.html("something went wrong !!!, please try later");
        else
          _ftStepContainer.html(data);
      }
    });
  }
  var _prepareView4TerminateStep = function(){
    var terminateDOM = _ftStepContainerTemp.children('.brad-terminate-step');
    if(!terminateDOM.length){
      /*      _appendStepCommon(terminateDOM.clone());
       }else{*/
      $(".jz").jzAjax("JuZLogoutApplication.loadTerminateView()",{
        success: function(data){
          if(typeof data == "string" && data != "nok"){
            _ftStepContainerTemp.append(data);
            _addOptionCountries();
            _initTerminateForm();
          }
          else
            _ftStepContainer.html("something went wrong, please retry it later");
        }
      });
    }
  };

  var _addOptionCountries = function() {
    var parent = _ftStepContainerTemp.children('.brad-terminate-step');
    var countryDOM = parent.find('.brad-participant-country');
    if(countryDOM.length > 0){
      var strOptions = '<option value="">Country</option>';
      $.getJSON("/brand-advocacy-webapp/resources/countries.json", function(data){
        $.each(data, function(i,v){
          strOptions +='<option value="'+v.name+'">'+v.name+'</option>';
        });
        countryDOM.append(strOptions);
      });
    }
  };


  var _initTerminateForm = function(){

    $("input:text").click(function(){
      $(this).closest('.control-group').removeClass('error');

    });

  };
  var _urlSubmitted;
  var _addEventToBtnSubmitStep3 = function(){
    $(document).on('click.juzbrad.ft.substep3.view','.btn-brad-submit-step3',function(){
      _msgError = "";
      var parent = _ftStepContainer.children('.brad-complete-step');
      var url = parent.find(".brad_participant_url_submitted").val();
      var urlDOM =  parent.find(".brad_participant_url_submitted");
      var errorClass = 'error';
      var urlParent = urlDOM.closest('.control-group');
      urlParent.removeClass(errorClass);
      if(urlDOM.val().length < 1 || !_validateUrlFormat(urlDOM.val())) {
        _msgError += "Please fill-in the correct url\n";
        urlParent.addClass(errorClass);
      }
      if(_msgError.length > 0){
        alert(_msgError);
      }else{
        var missionId = $(this).attr("data-missionId");
        var propositionId = $(".propositionId").val();
        if(_checkFtForm(missionId,propositionId)) {
          _urlSubmitted = urlDOM.val();
          var terminateDOM = _ftStepContainerTemp.children('.brad-terminate-step');
          _switchStepCommon('step3',terminateDOM.html());
        }else{
          _displayProcessError();
        }
      }
    })
  };

  var _completeMission = function(url){
    $('.jz').jzAjax("JuZLogoutApplication.completeMission()",{
      data:{url:url},
      success: function(data){
        if(data == "ok"){
          _completeMissionCB();
        }
        else if(data == 'nok')
          _ftStepContainer.html("something went wrong !!!, please try later");
        else
          _ftStepContainer.html(data);
      }
    });
  };

  var _completeMissionCB = function(){
    var terminateDOM = _ftStepContainerTemp.children('.brad-terminate-step');
    _switchStepCommon('end',terminateDOM.html());
  };

  var _addEventToBtnTerminate = function(){
    $(document).on('click.juzbrad.ft.terminate.view','.btn-brad-terminate',function(){
      if(_validateTerminateForm().length == 0){
        var parent = _ftStepContainer;//.children('.brad-terminate-step');
        var fname = parent.find(".brad-participant-fname").val();
        var lname = parent.find(".brad-participant-lname").val();
        var email = parent.find(".brad-participant-email").val();
        var address = parent.find(".brad-participant-address").val();
        var city = parent.find(".brad-participant-city").val();
        var phone = parent.find(".brad-participant-phone").val();
        var country = parent.find(".brad-participant-country").val();
        var size = parent.find(".brad-participant-size").val();
        _terminate(fname,lname,email,address,city,phone,country,size);
      }else{
        alert(_msgError);
      }
    });
  };

  var _isValidEmailAddress = function(emailAddress) {
    var pattern = new RegExp(/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i);
    return pattern.test(emailAddress);
  };

  var _validateTerminateForm = function(){
    _msgError = "";
    var parent = _ftStepContainer;
    var fNameDOM = parent.find(".brad-participant-fname");
    var lNameDOM = parent.find(".brad-participant-lname");
    var emailDOM = parent.find(".brad-participant-email");
    var adrsDOM  =  parent.find(".brad-participant-address");
    var phoneDOM = parent.find(".brad-participant-phone");
    var cityDOM = parent.find(".brad-participant-city");
    var countryDOM = parent.find(".brad-participant-country");

    var errorClass = 'error';
    var fNameParent = fNameDOM.closest('.control-group');
    fNameParent.removeClass(errorClass);
    var lNameParent = lNameDOM.closest('.control-group');
    lNameParent.removeClass(errorClass);
    var emailParent = emailDOM.closest('.control-group');
    emailParent.removeClass(errorClass);
    var adrsParent = adrsDOM.closest('.control-group');
    adrsParent.removeClass(errorClass);
    var phoneParent = phoneDOM.closest('.control-group')
    phoneParent.removeClass(errorClass);
    var cityParent = cityDOM.closest('.control-group')
    cityParent.removeClass(errorClass);

    if(fNameDOM.val().length < 1) {
      _msgError += "Please fill-in your first name\n";
      fNameParent.addClass(errorClass);
    }
    if(lNameDOM.val().length < 1) {
      _msgError += "Please fill-in your last name\n";
      lNameParent.addClass(errorClass);
    }
    if(emailDOM.val().length < 1) {
      _msgError += "Please fill-in your email\n";
      emailParent.addClass(errorClass);
    }else if(!_isValidEmailAddress(emailDOM.val())){
      _msgError += "Please fill-in a valid email address\n";
      emailParent.addClass(errorClass);
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
    if(countryDOM.val().length < 1) {
      _msgError += "Please select your country\n";
    }
    return _msgError;
  };
  var _terminate = function(fname,lname,email,address,city,phone,country,size){
    _displayProcessing();
    $('.jz').jzAjax("JuZLogoutApplication.terminate()",{
      data:{url:_urlSubmitted,fname:fname,lname:lname,email:email,address:address,city:city,phone:phone,country:country,size:size},
      success: function(data){
        if(data == "ok"){
          _terminateCB();
        }
        else if(data == 'nok'){
          _displayProcessError();
          return;
        }else
          _ftStepContainer.html(data);
      }
    });
  };

  var _terminateCB = function(){
    var thankyouDOM = _ftStepContainerTemp.children('.brad-thankyou-step');
    if(thankyouDOM.length){
      _checkGiveUp = false;
      _switchStepCommon('end',thankyouDOM.clone());
//      _appendStepCommon(thankyouDOM.clone());
      _sendNotifNewMissionParticipant();
      _initView();
      _tweetController();
      _addFocusEvent2Input();

    }else{
      alert('something went wrong, please try later');
    }
  };

  var _addEvent2BtnGiveUpYes = function(){
    $(document).on('click.giveup.yes','button.btn-brad-giveup-yes',function(){
      _checkGiveUp = false;
      _giveUpPopupDOM.hide();
      _reloadMission();
    });
  };
  var _addEvent2BtnGiveUpNo = function(){
    $(document).on('click.giveup.no','.btn-brad-giveup-no',function(){
      _checkGiveUp = true;
      _giveUpPopupDOM.hide();

    });

  };

  var _initUtmUrlParameter = function(){
    _disableUTMCookie = false;
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    _utm_content = 'no-params';
    _utm_term = 'no-params';
    _utm_campaign = 'no-params';
    _utm_medium  = 'no-params';
    _utm_source  = 'no-params';
    for (var i = 0; i < sURLVariables.length; i++)
    {
      var sParameterName = sURLVariables[i].split('=');
      var val = sParameterName[1];
      if (sParameterName[0] == 'utm_content')
      {
        _disableUTMCookie = true;
        _utm_content = val;
      }else if (sParameterName[0] == 'utm_term')
      {
        _disableUTMCookie = true;
        _utm_term = val;
      }else if (sParameterName[0] == 'utm_campaign')
      {
        _disableUTMCookie = true;
        _utm_campaign = val;
      }else if (sParameterName[0] == 'utm_medium')
      {
        _disableUTMCookie = true;
        _utm_medium = val;
      }else if (sParameterName[0] == 'utm_source')
      {
        _disableUTMCookie = true;
        _utm_source = val;
      }
    }
  };
  var _init = function(){
    _checkGiveUp = false;
    _ctrlDown = false;
    _ctrlKey = 17;
    _cKey = 67;
    _createNew = false;
    _isValidTweetMsg = true;

    _brandAdvLandingPageContainer = $("#brad-landing-page-container");
    _pickMissionSliderContainer = $("#brad-pick-mission");
    _brandAdvFtContainer = $("#brad-ft-container");
    _ftStepContainer = $(".brad-container-step-container");
    _brandStepBackgroundContainer = $("#brad-step-background-container");
    _brandLeftPanelContainer = $("#brad-left-panel-container");
    _ftStepContainerTemp = $("#brad-ft-container-temp");
    _giveUpPopupDOM = $('#giveupPopup');
    _initUtmUrlParameter();
    console.info(_utm_campaign+' - '+_utm_content+' - '+_utm_term+' - '+_utm_medium+' - '+_utm_source);
    if(_pickMissionSliderContainer.length > 0){

      var screenW = $(window).width();
      var screenH = $(window).height()-60;

      _brandAdvLandingPageContainer.children('.inner').css({'height':screenH});
      _addEvent2BtnPickMission();
      _addEventToBtnGo();
      _addEventToBtnSubmitStep3();


      _addEvent2DetectCopyAction();
      _addEventKeyDownCopy();
      _addEventClick2GetSuggestion();


      _addEventToBtnTerminate();
      _addEvent2BtnClose();
      _addEvent2LinkTweet();
      _addEvent2BtnGiveUpYes();
      _addEvent2BtnGiveUpNo();

      $('#slides-1').superslides({
        animation: 'fade',
        play:10000,
        pagination:false,
        hashchange: false
      });
    }
  }
  $(document).ready(function(){
    _init();
  });
})($);