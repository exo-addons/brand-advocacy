/**
 * Created by exoplatform on 13/10/14.
 */
(function($) {
  var _brandAdvFtContainer;
  var _discoveryDOM;
  var _ftStepContainerTemp;
  var _ftStepContainer;
  var _createNew;
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
  var _displayNoMoreMission = function(){
    _ftStepContainer.html('We are preparing next mission, please come back later');
  };
  var _displayProcessError = function(){
    _ftStepContainer.html('Something went wrong,please try later');
  };
  var _initTerminateForm = function(){

    $("input:text").click(function(){
      $(this).closest('.control-group').removeClass('error');

    });

  };
  var _validateTerminateForm = function(){
    _msgError = "";
    var parent = _ftStepContainer.children('.brad-terminate-step');
    var urlDOM =  parent.find(".brad_participant_url_submitted");
    var fNameDOM = parent.find(".brad-participant-fname");
    var lNameDOM = parent.find(".brad-participant-lname");
    var adrsDOM  =  parent.find(".brad-participant-address");
    var phoneDOM = parent.find(".brad-participant-phone");
    var cityDOM = parent.find(".brad-participant-city");
    var countryDOM = parent.find(".brad-participant-country");
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
    if(countryDOM.val().length < 1) {
      _msgError += "Please select your country\n";
    }
    return _msgError;
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
  var _loadDiscoveryView = function(){
    $(".jz").jzAjax("JuZFrontEndApplication.loadDiscoveryView()",{
      success: function(data){
        if(typeof data == "string" && data != "nok"){
          _brandAdvFtContainer.html(data);
          _discoveryDOM = $('.brad-ft-discovery');
          _ftStepContainer = $(".brad-container-step-container");
          _discoveryDOM.show();
          $(window).scroll(function (event) {
            var scroll = $(window).scrollTop();
            if(scroll > 45) {
              $(".tweetPopup").addClass('scroll-down');
            } else {
              $(".tweetPopup").removeClass('scroll-down');
            }
          });
        }
      }
    });
  };
  var _loadStepContainerView = function(){
    $('.jz').jzAjax("JuZFrontEndApplication.loadStepContainerView()", {
      success: function (data) {
        $("#brad-ft-container").html(data);
        _loadStartView();
      }
    });
  };

  var _loadStartView = function () {
    _displayLoading();
    $(".jz").jzAjax("JuZFrontEndApplication.loadStartView()",{
      success:function(data){
        _ftStepContainer.html(data);
        _ftStepContainerTemp.append(data);
      }
    });
  };

  var _loadMissionView = function(){
    $('.jz').jzAjax("JuZFrontEndApplication.loadProcessView()",{
      success: function(data){
        if(data != "nok"){
          _initView();
          _ftStepContainerTemp.append(data);
        }
      }
    });
  };

  var _executeMission = function(){
    $('.jz').jzAjax("JuZFrontEndApplication.executeMission()",{
      success: function(data){
        if(data == "ok"){
          _processExecuteMission();
        }
        else if(data == 'nok')
          _ftStepContainer.html("something went wrong !!!, please try later");
        else
          _ftStepContainer.html(data);
      }
    });
  }
  var _loadTerminateView = function(){
    $(".jz").jzAjax("JuZFrontEndApplication.loadTerminateView()",{
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
  };


  var _completeMission = function(url,fname,lname,address,city,phone,country,size){
    _displayProcessing();
    $('.jz').jzAjax("JuZFrontEndApplication.completeMission()",{
      data:{'url':url,fname:fname,lname:lname,address:address,city:city,phone:phone,country:country,size:size},
      success: function(data){
        if(data == "ok"){
          _processTerminate();
        }
        else if(data == 'nok'){
          _displayProcessError();
          return;
        }else
          _ftStepContainer.html(data);
      }
    });
  };
  var _loadThankyouView = function(){
    $('.jz').jzAjax("JuZFrontEndApplication.loadThankyouView()",{
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

  var _initView = function(){
    _removeStepCommon('brad-process-step');
  };

  var _addEventToBtnDiscovery = function(){
    $(document).on('click.juzbrad.ft.discovery.view','.brad-ft-discovery',function(){
      _displayLoading();
      $(this).hide();
      $('.brand-adv-LightBoxContainer').show();
      if(_createNew){
        _processGenerateNewMission();
      }else{
        _processDiscovery();
        $('.brand-adv-LightBoxContainer').show();
        _loadMissionView();
      }
    });
  };

  var _addEventToBtnClose = function(){
    $(document).on('click.juzbrad.ft.close.view','.btn-brad-close',function(){
      $(".tweetPopup").removeClass('scroll-down');
      _ftStepContainer.html('');
      $('.brand-adv-LightBoxContainer').hide();
      _discoveryDOM.show();
      _createNew = true;
    });
  };

  var _addEventToBtnStart = function(){
    $(document).on('click.juzbrad.ft.start.view','.btn-brad-start',function(){
      _displayProcessing();
      _processStart();
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
      var missionId = $(this).attr("data-missionId");
      var propositionId = $(".propositionId").val();
      if(label == "Next"){
        if(_checkFtForm(missionId,propositionId)) {
          _displayProcessing();
          _executeMission();
        }else{
          _displayProcessError();
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
        var parent = _ftStepContainer.children('.brad-terminate-step');
        var url = parent.find(".brad_participant_url_submitted").val();
        var fname = parent.find(".brad-participant-fname").val();
        var lname = parent.find(".brad-participant-lname").val();
        var address = parent.find(".brad-participant-address").val();
        var city = parent.find(".brad-participant-city").val();
        var phone = parent.find(".brad-participant-phone").val();
        var country = parent.find(".brad-participant-country").val();
        var size = parent.find(".brad-participant-size").val();
        _completeMission(url,fname,lname,address,city,phone,country,size);
      }else{
        alert(_msgError);
      }
    });
  };
  var _addOptionCountries = function() {
    var parent = _ftStepContainerTemp.children('.brad-terminate-step');
    var countryDOM = parent.find('.brad-participant-country');
    if(countryDOM.length > 0){
      var strOptions = '<option value="">Country</option>';
      $.getJSON("/brand-advocacy-webapp/resources/countries.json", function(data){
        $.each(data, function(i,v){
          strOptions +='<option value="'+v.code+'">'+v.name+'</option>';
        });
        countryDOM.append(strOptions);
      });
    }
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
      var parent = _ftStepContainer.children('.brad-thankyou-step');
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

  var _processDiscovery = function(){
    _appendStartStepView();
    var terminateDOM = _ftStepContainerTemp.children('.brad-terminate-step');
    if(terminateDOM.length <= 0){
      _loadTerminateView();
    }
  };
  var _processStart = function(){
    $('.jz').jzAjax('JuZFrontEndApplication.processStartMission()', {
      success:function(data){
        if(data == 'nok'){
          _displayProcessError();
        }else if(data == 'ok')
          _appendExecuteStepView();
        else
          _ftStepContainer.html(data);
      }
    });
  };
  var _processExecuteMission = function(){
    _appendTerminateStepView();
  };
  var _processTerminate = function(){
    _appendThankyouStepView();
  };
  var _processGenerateNewMission = function(){
    $('.jz').jzAjax('JuZFrontEndApplication.generateNewMission()',{
      success:function(data){
        if(data == 'nok'){
          _removeStepCommon('brad-ft-start-step');
          _displayNoMoreMission();
//          $('.brand-adv-LightBoxContainer').show();
        }else if(data == 'ok'){
          _processDiscovery();
//          $('.brand-adv-LightBoxContainer').show();
          _loadMissionView();
        }else{
          _ftStepContainer.html(data);
        }
      }
    });
  }
  var _appendStartStepView = function(){
    var startDOM = _ftStepContainerTemp.children('.brad-ft-start-step');
    if(startDOM.length){
      _appendStepCommon(startDOM.clone());
    }else{
      _displayNoMoreMission();
    }
  };
  var _appendExecuteStepView = function(){
    var processDOM = _ftStepContainerTemp.children('.brad-process-step');
    if(processDOM.length){
      _appendStepCommon(processDOM.clone());
    }else{
      _displayProcessError();
    }
  };
  var _appendTerminateStepView = function(){
    var terminateDOM = _ftStepContainerTemp.children('.brad-terminate-step');
    if(terminateDOM.length){
      _appendStepCommon(terminateDOM.clone());
    }else{
      _loadTerminateView();
    }
  };
  var _appendThankyouStepView = function(){
    var thankyouDOM = _ftStepContainerTemp.children('.brad-thankyou-step');
    if(thankyouDOM.length){
      _appendStepCommon(thankyouDOM.clone());
      _sendNotifNewMissionParticipant();
      _initView();
      _tweetController();
      _addFocusEvent2Input();

    }else{
      alert('something went wrong, please try later');
    }
  };
  var _ctrlDown
  var _ctrlKey;
  var _cKey;

  var _addEventClick2GetSuggestion = function(){
    $(document).on('click.juzBrad.Proposition.Click','.brad-proposition-suggestion',function(){
      var pDOM = $(this).children('p');
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

  bradObj.init = function(){
    _ctrlDown = false;
    _ctrlKey = 17;
    _cKey = 67;
    _createNew = false;
    _isValidTweetMsg = true;
    _ftStepContainerTemp = $("#brad-ft-container-temp");
    _brandAdvFtContainer = $("#brad-ft-container");
    _addEvent2DetectCopyAction();
    _addEventKeyDownCopy();
    _addEventClick2GetSuggestion();
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