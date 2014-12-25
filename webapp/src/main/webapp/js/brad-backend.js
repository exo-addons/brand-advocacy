/**
 * Created by exoplatform on 08/10/14.
 */
(function($) {

  var _currentMPStatus;
  var _bodyContainerDOM;
  var _managerContainerDOM;
  var _propositionContainerDOM;
  var _missionParticipantContainerDOM;
  var _currentMissionId;
  var _managerBradList2BeAdded;
  var _textAreaContentId;
  var _MissionPriorityEventTimeout;
  var _missionParticipantOldStatus;
  var brandAdvBackend = {};

  var _menuStyleController = function(action){
    var progDOM = $(".program-tab");
    progDOM.removeClass('active');
    var missionDOM = $(".mission-tab");
    missionDOM.removeClass('active');
    var mpDOM = $(".mission-participant-tab");
    mpDOM.removeClass('active');
    if (action == "program"){
      progDOM.addClass('active');
    }else if(action == "mission"){
      missionDOM.addClass('active');
    }else{
      mpDOM.addClass('active');
    }
  };
  var _messageConfirmCBController = function (type,message) {
    var alertDOM =  $('#brandAdvAlertContainer');
    if(type != null && type != "") {
      var icon = type.charAt(0).toUpperCase() + type.slice(1);
      var strIcon = "<i class='uiIcon" + icon + "'></i>";
      alertDOM.removeClass();
      alertDOM.addClass('alert');
      alertDOM.addClass('alert-' + type);
      alertDOM.html(strIcon + message);
      alertDOM.css('visibility', 'visible');
      setTimeout(function() {
        alertDOM.css("visibility" , "hidden");
      }, 5000);
    }
  };
  var _disPlayInfoMsgCB = function(msg){
    _messageConfirmCBController('info',msg);
  };
  var _disPlayWarningMsgCB = function(msg){
    _messageConfirmCBController('warning',msg);
  };
  var _disPlayErrorMsgCB = function(msg){
    _messageConfirmCBController('error',msg);
  };

  var _loadPopup = function(mode,title,content){
    var popupDOM = $('#iuBrandAdvPopupContainer');
    var contentDOM = popupDOM.children(".popupContent");
    if(mode == 'on'){
      var titleDOM = popupDOM.children(".popupTitle");
      titleDOM.html(title);
      contentDOM.html(content);
      popupDOM.show();
    }else{
      contentDOM.html('');
      popupDOM.hide();
    }
  };
  var _loadConfirmPopupContent = function(action,id,msg){
    _displayLoading(true);
    $('.jz').jzAjax('JuZBackEndApplication.loadConfirmPopupContent()',{
      data:{action:action,id:id,msg:msg},
      success:function(data){
        _displayConfirmPopup('on',data,msg);
        _displayLoading(false);
      }
    });
  };
  var _displayConfirmPopup = function(mode,content){
    var popupDOM = $('#iuBrandAdvConfirmPopupContainer');
    if(mode == 'on'){
      popupDOM.html(content);
      popupDOM.css({opacity:1});
      popupDOM.show();
    }else{
      popupDOM.html('');
      popupDOM.css({opacity:0});
      popupDOM.hide();
    }
  };

  var _fillBodyContainer = function(content){
    if (content == '')
      content = 'loading ... ';
    _bodyContainerDOM.html(content);
  }
  var _displayLoading = function(b){
    var loadingDOM = $('#BrandAdvAjaxLoadingMask');
    if(loadingDOM.length > 0){
      if(b)
        loadingDOM.show();
      else
        loadingDOM.hide();
    }
  };
  var _loadProgramContentView = function(){
    _displayLoading(true);
    _fillBodyContainer('');
    $(".jz").jzAjax("ProgramController.index()",{
      success:function(data){
        _fillBodyContainer(data);
        _managerContainerDOM = $('.program-list-managers');
        _loadProgramManagers();
      }
    });
  };
  var _addProgram = function(title){
    _displayLoading(true);
    $(".jz").jzAjax("ProgramController.add()",{
      data:{title:title},
      success:function(data){
        if(data != "nok"){
          _loadProgramContentView();
        }
        _displayLoading(false);
      }
    });
  };
  var _updateProgram = function(title,banner_url,email_sender){
    _displayLoading(true);
    $('.jz').jzAjax('ProgramController.update()',{
      data:{title:title,banner_url:banner_url,email_sender:email_sender},
      success:function(data){
        if(data != "nok"){
          _disPlayInfoMsgCB(data);
        }else
         _disPlayErrorMsgCB('something went wrong, cannot update the program');
        _displayLoading(false);
      }
    });
  }

  var _loadProgramManagers = function(){
    _displayLoading(true);
    _managerContainerDOM.html('loading ...');
    $('.jz').jzAjax('ManagerController.listProgramManagers()',{
      success:function(data){
        if(data !== 'nok'){
          _managerContainerDOM.html(data);
          _addEventIPhoneStyle2CheckBox();
          _managerBradList2BeAdded = [];
        }
        _displayLoading(false);
      }
    });
  };

  var _removeProgramManager = function(username){
    _displayLoading(true);
    $('.jz').jzAjax('ManagerController.removeProgramManager()',{
      data:{username:username},
      success:function(data){
        if(data !== 'nok'){
          _loadProgramManagers();
        }
        _displayLoading(false);
      }
    });
  };

  var _addProgramManager = function(usernames,role,notif){
    _displayLoading(true);
    $('.jz').jzAjax('ManagerController.add2Program()',{
      data:{username:usernames,role:role,notif:notif},
      success:function(data){
        if(data === 'ok'){
          _loadProgramManagers();
        }else{
          _disPlayErrorMsgCB(data);
        }
        _displayLoading(false);
      }
    });
  };

  var _addManagerFromSearchManager = function(username,fullname) {
    var found=false;
    $.each(_managerBradList, function (i, v) {
      if(v.username == username){
        found=true;
        return
      }
    });

    if(!found){
      $.each(_managerBradList2BeAdded, function (i, v) {
        if(v.username == username){
          found=true;
          return
        }
      });
    }
    if(!found){
      _managerBradList2BeAdded.push({'username':username,'fullname':fullname});
      _displayManagerList2BeAdded();
    }
  };
  var _removeManagerFromManagerList2BeAdded = function(username){

    var arr = jQuery.grep(_managerBradList2BeAdded, function(a) {
      return a.username !== username;
    });
    if(arr.length != _managerBradList2BeAdded.length){
      _managerBradList2BeAdded = arr;
      _displayManagerList2BeAdded();
    }
  };
  var _displayManagerList2BeAdded = function(){
    var str = '';
    $.each(_managerBradList2BeAdded,function(i,v){
      str +='<span contenteditable="false" class="remove-manager-2-be-added" data-userName="'+ v.username+'">';
      str +=v.fullname;
      str +='<i contenteditable="true" class="uiIconClose uiIconLightGray">x</i></span>';
    });
    $('.result-add-manager').html(str);
    if(str != ''){
      $('.result-add-manager').removeClass('hide');
    }else{
      $('.result-add-manager').addClass('hide');
    }
    $(".result-search-manager").removeClass('open');
  };
  var _addEvent2LinkAddManager2BeAdded = function(){
    $(document).on('click.juzBrad.bk.addManager2BeAdded','li.add-manager-2-be-added',function(){
      var username = $(this).attr('data-userName');
      var fullname = $(this).attr('data-fullName');
      _addManagerFromSearchManager(username,fullname);
      $(".result-search-manager").html('');
    });
  };
  var _addEvent2LinkRemoveManager2BeAdded = function(){
    $(document).on('click.juzBrad.bk.removeManager2BeAdded','span.remove-manager-2-be-added',function(){
      var username = $(this).attr('data-userName');
      _removeManagerFromManagerList2BeAdded(username);
    });
  };
  var _loadMissions = function(){
    _fillBodyContainer('');
    _displayLoading(true);
    $('.jz').jzAjax('MissionController.indexMission()',{
      success:function(data){
        _fillBodyContainer(data);
        _addEventIPhoneStyle2CheckBox();
        _displayLoading(false);
      }
    });
  };

  var _loadEditMissionFormView = function(missionId){
    _displayLoading(true);
    _fillBodyContainer('');
    $('.jz').jzAjax('MissionController.editForm()',{
      data:{missionId:missionId},
      success:function(data){
        if(data !== 'nok'){
          _fillBodyContainer(data);
          _addEventIPhoneStyle2CheckBox();
          _propositionContainerDOM = $('.proposition-container');
          _currentMissionId = missionId;
          _loadPropositions();
        }else{
          _disPlayErrorMsgCB('Something went wrong, cannot load mission detail view');
          _fillBodyContainer('item not found');
        }
        _displayLoading(false);
      }
    });
  };

  var _loadAddMissionFormView = function(){
    $('.jz').jzAjax('MissionController.addForm()',{
      success:function(data){
        _bodyContainerDOM.html(data);
        _displayLoading(false);
      }
    });
  };

  var _addMission = function(title,link,priority){
    _displayLoading(true);
    $('.jz').jzAjax('MissionController.addMission()',{
      data:{title:title,third_part_link:link,priority:priority},
      success:function(data){
        if(data === 'nok'){
          _disPlayErrorMsgCB('Something went wrong, cannot create mission')
          return;
        }
        _loadMissions();
        _displayLoading(false);
      }
    });
  };
  var _preAddMission = function(){
    var title = 'Write a review about eXo';
    var link = '';
    var priority = 0;
    _displayLoading(true);
    $('.jz').jzAjax('MissionController.addMission()',{
      data:{title:title,third_part_link:link,priority:priority},
      success:function(data){
        if(data === 'nok'){
          _disPlayErrorMsgCB('Something went wrong, cannot create mission')
          return;
        }else{
          var missionId = data;
          _loadEditMissionFormView(missionId);
        }
        _displayLoading(false);
      }
    });
  };

  var _updateMission = function(title,link,active){
    _displayLoading(true);
    $('.jz').jzAjax('MissionController.updateMission()',{
      data:{id:_currentMissionId,title:title,third_part_link:link,mission_active:active},
      success:function(data){
        if(data === 'nok'){
          _disPlayErrorMsgCB('Something went wrong, cannot update mission');
        }
        else{
          _disPlayInfoMsgCB('Mission has been updated');
        }
        _displayLoading(false);
      }
    });
  };
  var _updateMissionInline = function(missionId,action,val){
    _displayLoading(true);
    $('.jz').jzAjax("MissionController.ajaxUpdateInline()",{
      data:{missionId:missionId,action:action,val:val},
      success:function(data){
        try{
          var obj = data = $.parseJSON(data);
          if (obj.error){
            _disPlayErrorMsgCB(obj.msg);
          }else{
            _loadMissions();
          }
        }catch (e){}
        _displayLoading(false);
      }
    });
  };
  var __updateMissionStatusViaProposition = function(val){
    $('.jz').jzAjax("MissionController.ajaxUpdateInline()",{
      data:{missionId:_currentMissionId,action:'active',val:val},
      success:function(data){
        try{
          var obj = data = $.parseJSON(data);
          if (obj.error){
            _disPlayErrorMsgCB(obj.msg);
          }else{
            _loadEditMissionFormView(_currentMissionId);
          }
        }catch (e){}
        _displayLoading(false);
      }
    });
  };
  var _updateMissionPriority = function(missionId,priority){
    if(priority == "" || missionId == "")
      return;
    _displayLoading(true);
    console.info(' update mission priority '+priority);
    $('.jz').jzAjax("MissionController.updateMissionPriority()", {
      data: {missionId: missionId, priority: priority},
      success: function (data) {
        if(data != "Priority has been updated"){
          _disPlayErrorMsgCB(data);
        }else{
          _disPlayInfoMsgCB(data);
          _loadMissions();
        }
        _displayLoading(false);
      }
    });
  };
  var _sendNotifUpdateMissionParticipantEmail = function(missionParticipantId){
    $('.jz').jzAjax("JuZBackEndApplication.sendNotifUpdateMissionParticipantEmail()",{
      data:{missionParticipantId:missionParticipantId},
      success:function(){

      }
    });
  };
  var _updateMissionParticipantStatusInline = function(missionParticipantId,val){
    _displayLoading(true);
    var jStatus = $("select.mission-participant-status");
    $('.jz').jzAjax("MissionParticipantController.ajaxUpdateMPInline()",{
      data:{missionParticipantId:missionParticipantId,action:"status",val:val},
      success:function(data){
        try{
          var obj = data = $.parseJSON(data);
          if (obj.error){
            _disPlayErrorMsgCB(obj.msg);
            jStatus.val(obj.status);
          }else{
            if(obj.mpId != "")
              _sendNotifUpdateMissionParticipantEmail(obj.mpId);
            _disPlayInfoMsgCB(obj.msg);
          }
        }catch (e){
          _disPlayErrorMsgCB('something went wrong to update mission participant status');
        }
        _displayLoading(false);
      }
    });
  };
  var _putPriorityText2Readonly = function(missionId,b){
    var row = $('#mLine-'+missionId);
    if(row.length > 0){
      var prioInput = row.find(':text.brad-mission-priority');
      if(typeof prioInput != "undefined"){
        $(prioInput).prop('readonly',b);
      }
    }
  };
  var _removeMission = function(missionId){
    _displayLoading(true);
    $('.jz').jzAjax('MissionController.deleteMission()',{
      data:{missionId:missionId},
      success:function(data){
        if(data === 'nok'){
          _disPlayErrorMsgCB('Something went wrrong, cannot remove mission');
        }
        _loadMissions();
        _displayLoading(false);
      }
    });

  };
  var _loadPropositions = function(){
    var missionId = _currentMissionId;
;    $('.jz').jzAjax('PropositionController.indexProposition()',{
      data:{missionId:missionId},
      success:function(data){
        if(data === 'nok'){
          _disPlayErrorMsgCB('Something went wrong, cannot load proposition');
          return;
        }
        _propositionContainerDOM.html(data);
        _addEventIPhoneStyle2CheckBox();
        _missionStatusCheckBoxController(false);
        _displayLoading(false);
      }
    });
  };
  var _addProposition = function(content,active){
    var missionId = _currentMissionId;
    _displayLoading(true);
    $('.jz').jzAjax('PropositionController.addProposition()',{
      data:{missionId:missionId,content:content,active:active},
      success:function(data){
        if(data === 'nok'){
          _disPlayErrorMsgCB('Something went wrong, cannot add proposition');
          return;
        }
        _loadPopup('off','','');
        _loadPropositions();
        _displayLoading(false);
      }
    });
  };
  var _removeProposition = function(propositionId){
    _displayLoading(true);
    $('.jz').jzAjax('PropositionController.deleteProposition()',{
      data:{propositionId:propositionId},
      success:function(data){
        if(data === 'nok'){
          _disPlayErrorMsgCB('Something went wrong, cannot remove proposition');
          return;
        }
        _loadPropositions();
        _displayLoading(false);
      }
    });
  };

  var _updateProposition = function(propositionId,content,active){
    _displayLoading(true);
    $('.jz').jzAjax('PropositionController.updateProposition()',{
      data:{propositionId:propositionId,content:content,active:active},
      success:function(data){
        if(data === 'nok'){
          _disPlayErrorMsgCB('Something went wrong, cannot remove proposition');
          return;
        }
        _loadPopup('off','','');
        _loadPropositions();
        _displayLoading(false);
      }
    });
  };
  var _loadAddPropositionForm = function(){
    var missionId = _currentMissionId;
    _displayLoading(true);
    $('.jz').jzAjax('PropositionController.loadAddPropositionForm()',{
      data:{missionId:missionId},
      success:function(data){
        if(data === 'nok'){
          _disPlayErrorMsgCB('Something went wrong, cannot load form');
          return;
        }
        _loadPopup('on','Add a new proposition',data);
        _addCkEditor2Textarea();
        _addEventIPhoneStyle2CheckBox();
        _displayLoading(false);
      }
    });
  };
  var _loadEditPropositionForm = function(propositionId){
    _displayLoading(true);
    $('.jz').jzAjax('PropositionController.loadEditPropositionForm()',{
      data:{propositionId:propositionId},
      success:function(data){
        if(data === 'nok'){
          _disPlayErrorMsgCB('Something went wrong, cannot load edit form');
          return;
        }
        _loadPopup('on','Edit proposition',data);
        _addCkEditor2Textarea();
        _addEventIPhoneStyle2CheckBox();
        _displayLoading(false);
      }
    });
  };

  var _addCkEditor2Textarea = function(){
    try{
      CKEDITOR.replace(_textAreaContentId,{toolbar:'Basic'} );
    }catch(e) {
      alert('Something went wrong,please reload this page');
    }
  };
  var _getDataFromCkEditor = function(){
    try{
      var instance = CKEDITOR.instances[_textAreaContentId];
      return instance.getData();
    }catch(e) {
      alert('Something went wrong,please reload this page');
    }
    return "";
  };

  var _loadMissionParticipantContainer = function(){
    _displayLoading(true);
    _fillBodyContainer('');
    $('.jz').jzAjax('MissionParticipantController.indexMP()',{
      success:function(data){
        _fillBodyContainer(data);
        _missionParticipantContainerDOM = $('.mission-participant-container');
        _loadMissionParticipants('','',1);
      }
    });
  };

  var _loadMissionParticipants = function(keyword,status,page){
    _displayLoading(true);
    _missionParticipantContainerDOM.html(' loading ...');
    $('.jz').jzAjax('MissionParticipantController.search()',{
      data:{keyword:keyword,status:status,page:page},
      success:function(data){
        _missionParticipantContainerDOM.html(data);
        _displayLoading(false);
      }
    });
  };
  var _loadMissionParticipantDetail = function(missionParticipantId,username){
    _displayLoading(true);
    _fillBodyContainer('');
    $('.jz').jzAjax('MissionParticipantController.loadDetail()',{
      data:{missionParticipantId:missionParticipantId},
      success:function(data){
        if(data === 'nok'){
          _fillBodyContainer('item not found');
          _disPlayErrorMsgCB('Something went wrong, cannot load detail mission participant')
        }else{
          _fillBodyContainer(data);
          _loadPreviousMissionParticipant(username);
          _loadAllMPAdminNote(missionParticipantId);
        }
        _displayLoading(false);

      }
    });
  };
  var _loadPreviousMissionParticipant = function(username){
    _displayLoading(true);
    $(".previous-mission-participant").html('loading ....');
    $(".previous-mission-participant").jzAjax("MissionParticipantController.getPreviousMissionParticipant()",{
      data:{username:username},
      success:function(data){
        $(".previous-mission-participant").html(data);
        _displayLoading(false);
      }
    });
  };
  var _removeMissionParticipant = function(missionParticipantId){
    _displayLoading(true);
    $('.jz').jzAjax('MissionParticipantController.removeMissionParticipant()',{
      data:{missionParticipantId:missionParticipantId},
      success:function(data){
        if(data == 'ok'){
          _disPlayInfoMsgCB('mission participant has been successfully removed');
          var searchParams = _getCurrentSearchParams();
          _loadMissionParticipants(searchParams.keyword,searchParams.statusFilter,searchParams.page);
//          _loadMissionParticipants('','',1);
        }else{
          _disPlayErrorMsgCB(data);
        }
        _displayLoading(false);
      }
    });
  };

  var _addEvent2LinkClosePopup = function(){
    $(document).on('click.juzBrad.bk.closePopup','a.brandAdvPopupClose',function(){
      _loadPopup('off','','');
    });
  };
  var _addEvent2BtnIphoneCheckbox = function(){
    $(document).on('click.juzBrad.bk.chkBox','div .spaceIphoneChkBox',function(){
      var input = $(this).find("input:checkbox");
      var val = input.attr("value") == "true" ? "false" : "true";
      input.attr("value", val);
      var action = input.attr("data-action");
      if (action == "updateProgramManager"){
        var username =  input.attr("data-username");
        var notif = val;
        _displayLoading(true);
        $(input).jzAjax("ManagerController.updateAjaxProgramManagerInLine()",{
          data:{username:username,action:"notif",val:notif},
          success:function(data){
            if (data == "nok"){
              _disPlayErrorMsgCB("Something went wrong, cannot update manager");
            }else{
              _disPlayInfoMsgCB("Your change has successfully been updated");
            }
            _displayLoading(false);
          }
        });
      } else if(action == "updateMissionInline"){
        if($(input).prop('disabled')){
          _disPlayInfoMsgCB('Add propositions to enable this mission');
        }else{
          var missionId = input.attr("data-missionId");
          _updateMissionInline(missionId,"active",val);
        }
      } else if(action == "preUpdateMissionStatusInline"){
        if(nbBradPropositionsActive<= 0){
          _disPlayInfoMsgCB('Add propositions to enable this mission');
        }
      } else if (action == "updatePropositionInline"){
        var propositionId = input.attr("data-propositionId");
        var propositionActive = val;
        _displayLoading(true);
        $(input).jzAjax("PropositionController.ajaxUpdatePropositionInline()",{
          data:{propositionId:propositionId,action:"active",val:propositionActive},
          success:function(data){
            if (data == "nok"){
              _disPlayErrorMsgCB('something went wrong, cannot update proposition');
            }else{
              if(data == "true"){
                nbBradPropositionsActive++;
              }else{
                nbBradPropositionsActive--;
              }
              _disPlayInfoMsgCB("Your changes has successfully been updated");
              _missionStatusCheckBoxController(true);
            }
            _displayLoading(false);
          }
        });
      } else if(action == "addNewProgramManager"){

      }

    });
  };

  var _missionStatusCheckBoxController = function(reload){
    var chkBoxDOM = $('input:checkbox.mission-status-checkbox');
    var mDisable = chkBoxDOM.prop('disabled');
    var mIsChecked = chkBoxDOM.prop('checked');
    if(nbBradPropositionsActive > 0){
      if(mDisable){
        chkBoxDOM.prop('disabled',false);
      }
    }else{
      if(mIsChecked && reload){
        __updateMissionStatusViaProposition("false");
      }else{
        chkBoxDOM.prop('disabled',true);
      }
    }
  };
  var _addEvent2RoleSelect = function(){
    $(document).on('change.juzBrad.bk.manager.role','select.role',function(){
      var jRole = $(this);
      var managerRow = jRole.closest("tr");
      var checkBoxDOM = managerRow.find("input:checkbox");
      var username =  checkBoxDOM.attr("data-username");
      var role = managerRow.find("select[name=role]").val();
      _displayLoading(true);
      jRole.jzAjax("ManagerController.updateAjaxProgramManagerInLine()",{
        data:{username:username,action:"role",val:role},
        success:function(data){
          if (data == "nok"){
            _disPlayErrorMsgCB('Something went wrong, cannot update manager');
          }else{
            _disPlayInfoMsgCB('Manager role has been updated');
          }
          _displayLoading(false );
        }
      });
    });
  };

  var _addEventIPhoneStyle2CheckBox = function(){
    try{
      $("div.spaceIphoneChkBox").children('input:checkbox').each(function () {
        $(this).iphoneStyle({
          checkedLabel:"Yes",
          uncheckedLabel:"No"
        });
      });
    }catch(e) {
      $(".spaceIphoneChkBox").find(":checkbox").each(function(){
        $(this).css("visibility", "visible");
      });
    }
  };

  var _addEvent2MPStatus = function(){
    $(document).on('change.juzBrad.bk.mission-participant.status','select.mission-participant-status',function(){
      var jStatus = $(this);
      var missionParticipantId =jStatus.attr("data-mission-participant-id");
      if (typeof missionParticipantId != "undefined"){
        var val = jStatus.val();
        _updateMissionParticipantStatusInline(missionParticipantId,val);
      }else
        _disPlayErrorMsgCB('something went wrong to update mission participant status');

    });
  };

  var _searchingUserMamagerTimeOut;
  var _startSearchingUser = function(keyword){
    if(keyword == $('input.manager-username').val()){
      $(".jz").jzAjax('ManagerController.searchEXOProfiles()',{
        data:{keyword:keyword},
        success:function(data){
          $(".result-search-manager").html(data);
          $(".result-search-manager").addClass('open');
        }
      });
    }
  };
  var _addEventKeyPress2InputSearchManager = function(){
    $(document).on('keypress.juzBrad.bk.searchmanager.keypress','input.manager-username',function(){
      var keyword = $(this).val();
      if(keyword.length >= 3){
        $(".result-search-manager").html('searching '+$(this).val());
        $(".result-search-manager").addClass('open');
      }else{
        $(".result-search-manager").html('');
        $(".result-search-manager").removeClass('open');
      }
      if(_searchingUserMamagerTimeOut){
        clearTimeout(_searchingUserMamagerTimeOut);
      }
    });
  };
  var _addEventKeyUp2InputSearchManager = function(){
    $(document).on('keyup.juzBrad.bk.searchmanager.keyup','input.manager-username',function(){
      var keyword = $(this).val();
      if(keyword.length >= 3){
        _searchingUserMamagerTimeOut = setTimeout(function(){
          _startSearchingUser(keyword);
        },3000);
      }else{
        $(".result-search-manager").html('');
        $(".result-search-manager").removeClass('open');
      }
    });
  };


  var _addEvent2ProgramTabMenu = function(){
    $(document).on('click.juzBrad.bk.tabmenu.program','li.program-tab',function(e){
      _menuStyleController('program');
      _loadProgramContentView();
      e.preventDefault();
    });
  };

  var _addEvent2MissionTabMenu = function(){
    $(document).on('click.juzBrad.bk.tabmenu.mission','li.mission-tab',function(e){
      _menuStyleController('mission');
      _loadMissions();
      e.preventDefault();
    });
  };

  var _addEvent2MissionParticipantTabMenu = function(){
    $(document).on('click.juzBrad.bk.tabmenu.missionparticipant','li.mission-participant-tab,button.mission-participant-tab',function(e){
      _menuStyleController('mission-participant');
      _loadMissionParticipantContainer();
      e.preventDefault();
    });
  };

  var _addEvent2BtnAddProgram = function(){
    $(document).on('click.juzBrad.bk.btn.addprogram','button.btn-add-program',function(e){
      var title = $(".program-title").val();
      if(title.length === 0){
        return;
      }
      _addProgram(title);
      e.preventDefault();
    });
  }

  var _addEvent2BtnUpdateProgram = function(){
    $(document).on('click.juzBrad.bk.updateprogram','button.btn-update-program',function(e){
      var title = $(".program-title").val();
      var banner_url = $('.program-banner-url').val();
      var email_sender = $('.program-email-sender').val();
      if(title.length === 0){
        return;
      }
      _updateProgram(title,banner_url,email_sender);
      e.preventDefault();
    });
  };


  var _addEvent2BtnRemoveProgramManager = function(){
    $(document).on('click.juzBrad.bk.removeprogramuser','a.removeProgramUser',function(e){
      var username = $(this).attr("data-username");
      var fullname = $(this).attr('data-fullname');
      var msg = fullname+' will no longer have access to the brand advocacy admin. Are you sure you want to remove his role ';
      _loadConfirmPopupContent('removeprogramuser',username,msg);
      e.preventDefault();
    });
  };
  var _addEvent2BtnAddProgramManager = function(){
    $(document).on('click.juzBrad.bk.addprogramuser','button.btn-add-program-manager',function(e){
      if(_managerBradList2BeAdded.length > 0){
        var usernames = [];
        $.each(_managerBradList2BeAdded,function(i,v){
          usernames.push(v.username);
        });
        var username = _managerBradList2BeAdded[0].username;
        var role = $(".manager-role").val();
        var notif = $(".manager-notif").val();
        _addProgramManager(username,role,notif);
        e.preventDefault();
      }
    });
  };

  var _addEvent2LinkLoadAddMissionForm = function(){
    $(document).on('click.juzBrad.bk.loadAddMissionFormView','a.btn-load-add-mission-form',function(e){
      _loadAddMissionFormView();
      e.preventDefault();
    });
  };
  var _addEvent2LinkPreAddMission = function(){
    $(document).on('click.juzBrad.bk.preAddMission','a.pre-add-mission',function(e){
      _preAddMission();
      e.preventDefault();
    });
  };
  var _preSetMissionPriorityTimeout = function(missionId,priority){
    if(typeof missionId != "undefined" && typeof priority != "undefined"){
      _MissionPriorityEventTimeout = setTimeout(function(){
        if(priority > 0 && priority <= 100){
          _oldPriorityVal = "";
          _updateMissionPriority(missionId,priority);
        }
      },2000);
    }
  };
  var _addEvent2InputTextMissionPriority = function(){
    $(document).on('keypress.juzBrad.bk.missionPriority',':text.brad-mission-priority',function(event){
      console.info(' keypress '+event.which);
      if(_MissionPriorityEventTimeout)
        clearTimeout(_MissionPriorityEventTimeout);
      // only number
      $(this).val($(this).val().replace(/[^\d].+/, ""));
      // Allow: backspace, delete, tab, escape, enter,left,right
      if($.inArray(event.which, [46, 8 ,13,37,39]) !== -1)
        event.preventDefault();
      if ((event.which >= 48 && event.which <= 57) ){
      }
    });
  };
  var _addEventKeyUp2InputTextMissionPriority = function(){
    $(document).on('keyup.juzBrad.bk.missionPriorityKeyup',':text.brad-mission-priority',function(event){
      if (event.which != 8 && event.which != 46 && _oldPriorityVal != $(this).val()){
        console.info(' key up event start update mission prio '+_oldPriorityVal+' new '+$(this).val() );
        _oldPriorityVal = $(this).val();
        _preSetMissionPriorityTimeout($(this).attr('data-missionId'), $(this).val());

      }
      else {
        console.info('do nothing');
        _MissionPriorityEventTimeout = null;
      }
    });
  };
  var _oldPriorityVal;
  var _addEventClick2InputTextMissionPriority = function(){
    $(document).on('click.juzBrad.bk.missionPriorityClick',':text.brad-mission-priority',function(event){
      if($(this).prop('readonly')){
        _disPlayInfoMsgCB('Activate mission to edit its priority');
      }
      _oldPriorityVal = $(this).val();
      console.info(' click old value '+_oldPriorityVal);
     // $(this).val('');
    });
  };
  var _addEventBlur2InputTextMissionPriority = function(){
    $(document).on('blur.juzBrad.bk.missionPriorityBlur',':text.brad-mission-priority',function(event){
      if($(this).val() == ""){
        $(this).val(_oldPriorityVal);
      }else if( (!_MissionPriorityEventTimeout || _MissionPriorityEventTimeout == null)  &&  _oldPriorityVal != $(this).val()){
        console.info(' blur event start update mission prio '+_oldPriorityVal+' new '+$(this).val() );
        _oldPriorityVal = $(this).val();
        _preSetMissionPriorityTimeout($(this).attr('data-missionId'), $(this).val());
      }
    });
  };

  var _addEvent2BtnAddMission = function(){
    $(document).on('click.juzBrad.bk.addMission','button.btn-add-mission',function(e){
      var title = $('#title').val();
      var link = $('#third_part_link').val();
      var priority = $('#priority').val();
      _addMission(title,link,priority);
      e.preventDefault();
    });
  };
  var _addEvent2BtnLoadEditMissionForm = function(){
    $(document).on('click.juzBrad.bk.editmission','a.load-edit-mission-form',function(e){
      var missionId = $(this).attr('data-missionId');
      _loadEditMissionFormView(missionId);
      e.preventDefault();
    });
  };
  var _addEvent2BtnUpdateMission = function(){
    $(document).on('click.juzBrad.bk.updateMission','button.btn-update-mission',function(e){
      var title = $('#title').val();
      var link = $('#third_part_link').val();
      var active = null;
      if($('#mission_active').length > 0)
        active = $('#mission_active').val();
      _updateMission(title,link,active);
      e.preventDefault();
    });
  };
  var _addEvent2LinkRemoveMission = function(){
    $(document).on('click.juzBrad.bk.removeMission','a.removeMission',function(e){
      var missionId = $(this).attr('data-missionId');
      _loadConfirmPopupContent('removeMission',missionId,'Are you sure to remove this mission')
      e.preventDefault();
    });
  };
  var _addEvent2BtnCancelUpdateMisssion = function(){
    $(document).on('click.juzbrad.bk.cancelupdatemission','a.back-to-missions',function(e){
      _loadMissions();
      e.preventDefault();
    });
  };

  var _addEvent2LinkLoadAddPropositionForm = function(){
    $(document).on('click.juzBrad.bk.loadAddPropositionForm','a.a-load-add-proposition-form',function(e){
      _loadAddPropositionForm();
      e.preventDefault();
    });
  };
  var _addEvent2BtnAddProposition = function(){
    $(document).on('click.juzBrad.bk.addProposition','button.btn-add-proposition',function(e){
      var formDOM = $('.uiPopup').find('.addNewProposition');
      var content = _getDataFromCkEditor();
      if(content == ""){
        content = formDOM.find('textarea').val();
      }
      var active = formDOM.find(':checkbox').val();
      _addProposition(content,active);
      e.preventDefault();
    })
  };
  var _addEvent2LinkLoadEditPropositionForm = function(){
    $(document).on('click.juzBrad.bk.loadEditPropositionForm','a.a-load-edit-proposition-form',function(e){
      var propositionId = $(this).attr('data-propositionId');
      _loadEditPropositionForm(propositionId);
      e.preventDefault();
    });
  };
  var _addEvent2BtnUpdateProposition = function(){
    $(document).on('click.juzBrad.bk.updateProposition','button.btn-update-proposition',function(e){
      var propositionId = $(this).attr('data-propositionId');
      var formDOM = $('.uiPopup').find('.addNewProposition');
      var content = _getDataFromCkEditor();
      if(content == ""){
        content = formDOM.find('textarea').val();
      }
      var active = formDOM.find(':checkbox').val();
      _updateProposition(propositionId,content,active);
      e.preventDefault();
    });
  };
  var _addEvent2LinkRemoveProposition = function(){
    $(document).on('click.juzBrad.bk.removeProposition','a.remove-proposition',function(e){
      var propositionId = $(this).attr('data-propositionId');
      _loadConfirmPopupContent('removeProposition',propositionId,'Are you sure to remove this proposition');
      e.preventDefault();
    });
  };
  var _addEvent2BtnViewMissionParticipant = function(){
    $(document).on('click.juzBrad.bk.viewMP','a.mp-view',function(e){
      var missionParticipantId = $(this).attr('data-mission-participant-id');
      var username = $(this).attr('data-participant-id');
      _loadMissionParticipantDetail(missionParticipantId,username);
      e.preventDefault();
    })
  };

  var _addEvent2BtnSearchMissionParticipant = function(){
    $(document).on('click.juzBrad.bk.searchMissionParticipant.btn','button.btn-search-mission-participant',function(e){
      /*
      var searchForm = $(this).parent('.uiSearchInput');
      var keyword = searchForm.find(':text').val();
      var statusFilter = searchForm.find('select').val();
      _loadMissionParticipants(keyword,statusFilter,1);
      */
      var searchParams = _getCurrentSearchParams();
      _loadMissionParticipants(searchParams.keyword,searchParams.statusFilter,searchParams.page);
      e.preventDefault();
    });
  };
  var _addEvent2InputTextKeywordSearchMissionParticipant = function(){
    $(document).on('keypress keyup.juzBrad.bk.searchMissionParticipant.keyword','input.keyword-search-mission-participant',function(e){
      var key = e.which;
      if(key == 13)
      {
        var searchForm = $(this).parent('.uiSearchInput');
        var keyword = searchForm.find(':text').val();
        var statusFilter = searchForm.find('select').val();
        _loadMissionParticipants(keyword,statusFilter,1);
        e.preventDefault();
      }
    });
  };
  var _getCurrentSearchParams = function(){
    var searchForm = $('.uiSearchInput');
    var keyword = searchForm.find(':text').val();
    var statusFilter = searchForm.find('select').val();
    var pageDOM = $('.uiPageIterator').find('li.active');
    var page = pageDOM.children('a').text();
    return {'keyword':keyword,'statusFilter':statusFilter,'page':page};
  };
  var _addEvent2LinkPageSearchMissionParticipant = function(){
    $(document).on('click.juzBrad.bk.searchMissionParticipant.page','li.search-mission-participant-page',function(e){
      var page = $(this).attr('data-page');
      var searchParams = _getCurrentSearchParams();
      _loadMissionParticipants(searchParams.keyword,searchParams.statusFilter,page);
      e.preventDefault();
    });
  };
  var _addEvent2SelectStatusSearchMissionParticipant = function(){
    $(document).on('change.juzBrad.bk.searchMissionParticipant.status','select.status-search-mission-participant',function(e){
      var searchParams = _getCurrentSearchParams();
      _loadMissionParticipants(searchParams.keyword,searchParams.statusFilter,1);
      e.preventDefault();
    });
  };
  var _addEvent2LinkRemoveMissionParticipant = function(){
    $(document).on('click.juzBrad.bk.removeMissionParticipant','a.removeMissionParticipant',function(e){
      var missionParticipantId = $(this).attr('data-mission-participant-id');
      _loadConfirmPopupContent('removeMissionParticipant',missionParticipantId,'Are you sure to remove this mission participant');
      e.preventDefault();
    });
  };
  var _addEvent2LinkConfirmYes = function(){
    $(document).on('click.juzBrad.bk.confirmYes','a.btn-brad-Confirm-Yes',function(e){
      var action = $(this).attr('data-action');
      var id = $(this).attr('data-id');
      if(typeof action !== "undefined" && typeof id !== "undefined"){
        if(action == "removeprogramuser"){
          _removeProgramManager(id);
        }else if(action == "removeMission"){
          _removeMission(id);
        }else if(action == "removeProposition"){
          _removeProposition(id);
        }else if(action == "removeMissionParticipant"){
          _removeMissionParticipant(id);
        }
       }
      else{
        _disPlayErrorMsgCB('Something went wrong, cannot process this action');
      }
      _displayConfirmPopup('off','');
      e.preventDefault();
    });

  };
  var _addEvent2LinkConfirmNo = function(){
    $(document).on('click.juzBrad.bk.confirmNo','a.btn-brad-Confirm-No,a.brandAdvConfirmPopupClose',function(e){
      _displayConfirmPopup('off','','');
    });
  };

  var _loadAllMPAdminNote = function(mpId){
    var _mpAdminNoteContainerDOM = $(".brandadv-mp-admin-note-list");
    $('.jz').jzAjax("MissionParticipantController.getAllMPAdminNote()", {
      data:{mpId:mpId},
      success:function(data){
        if(data != "nok"){
          _mpAdminNoteContainerDOM.html(data);
        }
      }
    });
  };
  var _addMPAdminNote = function(mpId,content){
    $('.jz').jzAjax("MissionParticipantController.addMPAdminNote()",{
      data:{mpId:mpId,content:content},
      success:function(data){
        if (data == "nok"){
          _disPlayErrorMsgCB('something went wrong, cannot add note to mission participant');
        }else{
          _loadAllMPAdminNote(mpId);
        }
        _displayLoading(false);
      }
    });
  };
  var _addEventClick2BtnAddMPAdminNote = function(){
    $(document).on('click.juzBrad.bk.addMPAdminNote','button.brandadv-add-mp-admin-note-add',function(e){
      var content = $("textarea.brandadv-mp-admin-note-content").val();
      if(content.trim().length > 0){
        var mpId = $(this).attr("data-mission-participant-id");
        _addMPAdminNote(mpId,content);
      }else{
        _disPlayInfoMsgCB("please fill note content");
      }
    });
  };
  var _initProgramEvent = function(){
    _addEvent2ProgramTabMenu();
    _addEvent2BtnAddProgram();
    _addEvent2BtnUpdateProgram();
  };
  var _initManagerEvent = function(){
    _addEvent2BtnAddProgramManager();
    _addEvent2BtnRemoveProgramManager();
    _addEventKeyPress2InputSearchManager();
    _addEventKeyUp2InputSearchManager();
    _addEvent2LinkAddManager2BeAdded();
    _addEvent2LinkRemoveManager2BeAdded();
  };
  var _initMissionEvent = function(){
    _addEvent2MissionTabMenu();
    _addEvent2LinkLoadAddMissionForm();
    _addEvent2BtnLoadEditMissionForm();
    _addEvent2BtnAddMission();
    _addEvent2LinkRemoveMission();
    _addEvent2BtnUpdateMission();
    _addEvent2BtnCancelUpdateMisssion();
    _addEvent2LinkPreAddMission();
    _addEvent2InputTextMissionPriority();
    _addEventClick2InputTextMissionPriority();
    _addEventKeyUp2InputTextMissionPriority();
    _addEventBlur2InputTextMissionPriority();
  };
  var _initPropositionEvent = function(){
    _addEvent2LinkLoadAddPropositionForm();
    _addEvent2BtnAddProposition();
    _addEvent2LinkLoadEditPropositionForm();
    _addEvent2BtnUpdateProposition();
    _addEvent2LinkRemoveProposition();
  };
  var _initMissionParticipantEvent = function(){
    _addEvent2MissionParticipantTabMenu();
    _addEvent2BtnViewMissionParticipant();
    _addEvent2BtnSearchMissionParticipant();
    _addEvent2InputTextKeywordSearchMissionParticipant();
    _addEvent2SelectStatusSearchMissionParticipant();
    _addEvent2LinkPageSearchMissionParticipant();
    _addEvent2LinkRemoveMissionParticipant();
  };
  var _initMissionParticipantNoteEvent = function(){
    _addEventClick2BtnAddMPAdminNote();
  }

  var _initVar = function(){
    _textAreaContentId = "bradAdvPropositionContent";
    _bodyContainerDOM = $(".tab-content");
    _MissionPriorityEventTimeout = false;
  }
  brandAdvBackend.init = function(isAdmin,action,id,username){
    _initVar();
    if(isAdmin == 'true'){
      _addEvent2LinkClosePopup();
      _initProgramEvent();
      _initManagerEvent();
      _initMissionEvent();
      _initPropositionEvent();
      _addEventIPhoneStyle2CheckBox();
      _addEvent2BtnIphoneCheckbox();
      _addEvent2RoleSelect();
      _initMissionParticipantNoteEvent();
    }else{
      _menuStyleController('participant');
    }
    _initMissionParticipantEvent();
    _addEvent2MPStatus();
    _addEvent2LinkConfirmYes();
    _addEvent2LinkConfirmNo();

    if(action == "mp_view"){
      _menuStyleController('participant');
      _loadMissionParticipantDetail(id,username);
    } else if(action == "m_view"){
      _menuStyleController("mission");
      _loadEditMissionFormView(id);
    } else if(action == "mission"){
      _menuStyleController("mission");
      _loadMissions();
    } else if(action == "participant"){
      _menuStyleController('participant');
      _loadMissionParticipantContainer();
    }else {
      _menuStyleController("program");
      _loadProgramContentView();
    }
  };

  return brandAdvBackend;
})($);