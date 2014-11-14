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
      alertDOM.fadeIn(500).delay(1000).fadeOut(500);
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
  var _loadProgramContentView = function(){
    $(".jz").jzAjax("ProgramController.index()",{
      success:function(data){
        _bodyContainerDOM.html(data);
        _managerContainerDOM = $('.program-list-managers');
        _loadProgramManagers();

      }
    });
  };
  var _addProgram = function(title){
    $(".jz").jzAjax("ProgramController.add()",{
      data:{title:title},
      success:function(data){
        if(data != "nok"){
          _loadProgramContentView();
        }
      }
    });
  };
  var _updateProgram = function(title){
    $('.jz').jzAjax('ProgramController.update()',{
      data:{title:title},
      success:function(data){
        if(data != "nok"){
          _disPlayInfoMsgCB('The program has been updated');
          return;
        }
        _disPlayErrorMsgCB('something went wrong, cannot update the program');
      }
    });
  }

  var _loadProgramManagers = function(){
    $('.jz').jzAjax('ManagerController.listProgramManagers()',{
      success:function(data){
        if(data !== 'nok'){
          _managerContainerDOM.html(data);
          _addEventIPhoneStyle2CheckBox();
          _managerBradList2BeAdded = [];

        }
      }
    });
  };

  var _removeProgramManager = function(username){
    $('.jz').jzAjax('ManagerController.removeProgramManager()',{
      data:{username:username},
      success:function(data){
        if(data !== 'nok'){
          _loadProgramManagers();
        }
      }
    });
  };

  var _addProgramManager = function(usernames,role,notif){
    $('.jz').jzAjax('ManagerController.add2Program()',{
      data:{username:usernames,role:role,notif:notif},
      success:function(data){
        if(data === 'ok'){
          _loadProgramManagers();
        }else{
          _disPlayErrorMsgCB(data);
        }
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
    $('.jz').jzAjax('MissionController.indexMission()',{
      success:function(data){
        _bodyContainerDOM.html(data);
        _addEventIPhoneStyle2CheckBox();
      }
    });
  };

  var _loadEditMissionFormView = function(missionId){
    $('.jz').jzAjax('MissionController.editForm()',{
      data:{missionId:missionId},
      success:function(data){
        if(data !== 'nok'){
          _bodyContainerDOM.html(data);
          _addEventIPhoneStyle2CheckBox();
          _propositionContainerDOM = $('.proposition-container');
          _currentMissionId = missionId;
          _loadPropositions();
        }else{
          _disPlayErrorMsgCB('Something went wrong, cannot load mission detail view');
        }
      }
    });
  };

  var _loadAddMissionFormView = function(){
    $('.jz').jzAjax('MissionController.addForm()',{
      success:function(data){
        _bodyContainerDOM.html(data);
      }
    });
  };

  var _addMission = function(title,link,priority){
    $('.jz').jzAjax('MissionController.addMission()',{
      data:{title:title,third_part_link:link,priority:priority},
      success:function(data){
        if(data === 'nok'){
          _disPlayErrorMsgCB('Something went wrong, cannot create mission')
          return;
        }
        _loadMissions();
      }
    });
  };

  var _updateMission = function(title,link,active){
    $('.jz').jzAjax('MissionController.updateMission()',{
      data:{id:_currentMissionId,title:title,third_part_link:link,mission_active:active},
      success:function(data){
        if(data === 'nok'){
          _disPlayErrorMsgCB('Something went wrong, cannot update mission');
        }
        else{
          _disPlayInfoMsgCB('Mission has been updated');
        }
      }
    });
  };
  var _updateMissionInline = function(missionId,action,val){
    $('.jz').jzAjax("MissionController.ajaxUpdateInline()",{
      data:{missionId:missionId,action:action,val:val},
      success:function(data){
        try{
          var obj = data = $.parseJSON(data);
          if (obj.error){
            if(obj.msg=="mission-prio-exceeded"){
              _disPlayWarningMsgCB('the total priorities has been exceeded');
            }else
              _disPlayErrorMsgCB(obj.msg);
          }
        }catch (e){}
      }
    });
  };
  var _removeMission = function(missionId){
    $('.jz').jzAjax('MissionController.deleteMission()',{
      data:{missionId:missionId},
      success:function(data){
        if(data === 'nok'){
          _disPlayErrorMsgCB('Something went wrrong, cannot remove mission');
        }
        _loadMissions();
      }
    });

  };
  var _loadPropositions = function(){
    var missionId = _currentMissionId;
    $('.jz').jzAjax('PropositionController.indexProposition()',{
      data:{missionId:missionId},
      success:function(data){
        if(data === 'nok'){
          _disPlayErrorMsgCB('Something went wrong, cannot load proposition');
          return;
        }
        _propositionContainerDOM.html(data);
        _addEventIPhoneStyle2CheckBox();
        _missionStatusCheckBoxController(false);

      }
    });
  };
  var _addProposition = function(content,active){
    var missionId = _currentMissionId;
    $('.jz').jzAjax('PropositionController.addProposition()',{
      data:{missionId:missionId,content:content,active:active},
      success:function(data){
        if(data === 'nok'){
          _disPlayErrorMsgCB('Something went wrong, cannot add proposition');
          return;
        }
        _loadPopup('off','','');
        _loadPropositions();
      }
    });
  };
  var _removeProposition = function(propositionId){
    $('.jz').jzAjax('PropositionController.deleteProposition()',{
      data:{propositionId:propositionId},
      success:function(data){
        if(data === 'nok'){
          _disPlayErrorMsgCB('Something went wrong, cannot remove proposition');
          return;
        }
        _loadPropositions();
      }
    });
  };

  var _updateProposition = function(propositionId,content,active){
    $('.jz').jzAjax('PropositionController.updateProposition()',{
      data:{propositionId:propositionId,content:content,active:active},
      success:function(data){
        if(data === 'nok'){
          _disPlayErrorMsgCB('Something went wrong, cannot remove proposition');
          return;
        }
        _loadPopup('off','','');
        _loadPropositions();
      }
    });
  };
  var _loadAddPropositionForm = function(){
    var missionId = _currentMissionId;
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
      }
    });
  };
  var _loadEditPropositionForm = function(propositionId){
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
      }
    });
  };

  var _addCkEditor2Textarea = function(){
    try{
      CKEDITOR.replace(_textAreaContentId,{toolbar:'Basic'} );
    }catch(e) {}
  };
  var _getDataFromCkEditor = function(){
    try{
      var instance = CKEDITOR.instances[_textAreaContentId];
      return instance.getData();
    }catch(e) {}
    return "";
  };

  var _loadMissionParticipantContainer = function(){
    $('.jz').jzAjax('MissionParticipantController.indexMP()',{
      success:function(data){
        _bodyContainerDOM.html(data);
        _missionParticipantContainerDOM = $('.mission-participant-container');
        _loadMissionParticipants('','',0);
      }
    });
  };

  var _loadMissionParticipants = function(keyword,status,page){
    $('.jz').jzAjax('MissionParticipantController.search()',{
      data:{keyword:keyword,status:status,page:page},
      success:function(data){
        _missionParticipantContainerDOM.html(data);
      }
    });
  };
  var _loadMissionParticipantDetail = function(missionParticipantId,username){
    $('.jz').jzAjax('MissionParticipantController.loadDetail()',{
      data:{missionParticipantId:missionParticipantId},
      success:function(data){
        if(data === 'nok'){
          _disPlayErrorMsgCB('Something went wrong, cannot load detail mission participant')
          return;
        }
        _bodyContainerDOM.html(data);
        _loadPreviousMissionParticipant(username);
      }
    });
  };
  var _loadPreviousMissionParticipant = function(username){

    $(".previous-mission-participant").jzAjax("MissionParticipantController.getPreviousMissionParticipant()",{
      data:{username:username},
      success:function(data){
        $(".previous-mission-participant").html(data);
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
        $(input).jzAjax("ManagerController.updateAjaxProgramManagerInLine()",{
          data:{username:username,action:"notif",val:notif},
          success:function(data){
            if (data == "nok"){
              _disPlayErrorMsgCB("Something went wrong, cannot update manager");
            }else{
              _disPlayInfoMsgCB("Your change has successfully been updated");
            }
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
        _updateMissionInline(_currentMissionId,'active',"false");
        _loadEditMissionFormView(_currentMissionId);
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
      jRole.jzAjax("ManagerController.updateAjaxProgramManagerInLine()",{
        data:{username:username,action:"role",val:role},
        success:function(data){
          if (data == "nok"){
            _disPlayErrorMsgCB('Something went wrong, cannot update manager');
          }else{
            _disPlayInfoMsgCB('Manager role has been updated');
          }
        }
      });
    });
  };

  var _addEvent2PrioritySelect = function(){
    $(document).on('change.juzBrad.bk.mission.priority','select.priority',function(){

      var jPriority = $(this);
      var missionRow = jPriority.closest("tr");
      var checkBoxDOM = missionRow.find("input:checkbox");
      var missionId =  checkBoxDOM.attr("data-missionId");
      var priority = missionRow.find("select[name=priority]").val();
      _updateMissionInline(missionId,'priority',priority);

/*      jPriority.jzAjax("MissionController.ajaxUpdateInline()",{
        data:{missionId:missionId,action:"priority",val:priority},
        success:function(data){
          try{
            var obj = data = $.parseJSON(data);
            if($(".mission-prio-exceeded"))
              $(".mission-prio-exceeded").hide();
            if (obj.error){
              if(obj.msg=="mission-prio-exceeded"){
                _disPlayWarningMsgCB('');
                $(".mission-prio-exceeded").show();
              }else
                alert(obj.msg);
            }
          }catch (e){}
        }
      });*/
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
    $(document).on('focus.juzBrad.bk.mission-participant.status','select.mission-participant-status',function(){
      _currentMPStatus = $(this).val();
    }).change(function(evt){
      var jStatus = $(evt.srcElement);
      var missionParticipantId =jStatus.attr("data-mission-participant-id");
      if (typeof missionParticipantId != "undefined"){
        var val = jStatus.val();
        jStatus.jzAjax("MissionParticipantController.ajaxUpdateMPInline()",{
          data:{missionParticipantId:missionParticipantId,action:"status",val:val},
          success:function(data){
            if (data != "ok"){
              _disPlayErrorMsgCB(data);
              jStatus.val(_currentMPStatus);
            }else
              _disPlayInfoMsgCB("your change has successfully been updated");
          }
        });
      }

    });
  };

  var _addEvent2InputUsername = function(){
    var termTemplate = "<span class='ui-autocomplete-term'>%s</span>";
    $(document).on('keypress.juzBrad.bk.searchmanager','input.manager-username',function(){
      var keyword = $(this).val();
      if(keyword.length >= 3){
        $(".jz").jzAjax('ManagerController.searchEXOProfiles()',{
          data:{keyword:keyword},
          success:function(data){
            $(".result-search-manager").html(data);
            $(".result-search-manager").addClass('open');
          }
        });
      }else{
        $(".result-search-manager").html('');
        $(".result-search-manager").removeClass('open');
      }
    })
  };

  var _addEvent2ProgramTabMenu = function(){
    $(document).on('click.juzBrad.bk.tabmenu.program','li.program-tab',function(){
      _menuStyleController('program');
      _loadProgramContentView();
    });
  };

  var _addEvent2MissionTabMenu = function(){
    $(document).on('click.juzBrad.bk.tabmenu.mission','li.mission-tab',function(){
      _menuStyleController('mission');
      _loadMissions();
    });
  };

  var _addEvent2MissionParticipantTabMenu = function(){
    $(document).on('click.juzBrad.bk.tabmenu.missionparticipant','li.mission-participant-tab,button.mission-participant-tab',function(){
      _menuStyleController('mission-participant');
      _loadMissionParticipantContainer();
    });
  };

  var _addEvent2BtnAddProgram = function(){
    $(document).on('click.juzBrad.bk.btn.addprogram','button.btn-add-program',function(){
      var title = $(".program-title").val();
      if(title.length === 0){
        return;
      }
      _addProgram(title);
    });
  }

  var _addEvent2BtnUpdateProgram = function(){
    $(document).on('click.juzBrad.bk.updateprogram','button.btn-update-program',function(){
      var title = $(".program-title").val();
      if(title.length === 0){
        return;
      }
      _updateProgram(title);
    });
  };


  var _addEvent2BtnRemoveProgramManager = function(){
    $(document).on('click.juzBrad.bk.removeprogramuser','a.removeProgramUser',function(){
      var username = $(this).attr("data-username");
      _removeProgramManager(username);
    });
  };
  var _addEvent2BtnAddProgramManager = function(){
    $(document).on('click.juzBrad.bk.addprogramuser','button.btn-add-program-manager',function(){
      if(_managerBradList2BeAdded.length > 0){
        var usernames = [];
        $.each(_managerBradList2BeAdded,function(i,v){
          usernames.push(v.username);
        });
        var username = _managerBradList2BeAdded[0].username;
        var role = $(".manager-role").val();
        var notif = $(".manager-notif").val();
        _addProgramManager(username,role,notif);
      }
    });
  };
  var _addEvent2LinkLoadAddMissionForm = function(){
    $(document).on('click.juzBrad.bk.loadAddMissionFormView','a.btn-load-add-mission-form',function(){
      _loadAddMissionFormView();
    });
  };
  var _addEvent2BtnAddMission = function(){
    $(document).on('click.juzBrad.bk.addMission','button.btn-add-mission',function(){
      var title = $('#title').val();
      var link = $('#third_part_link').val();
      var priority = $('#priority').val();
      _addMission(title,link,priority);
    });
  };
  var _addEvent2BtnLoadEditMissionForm = function(){
    $(document).on('click.juzBrad.bk.editmission','a.load-edit-mission-form',function(){
      var missionId = $(this).attr('data-missionId');
      _loadEditMissionFormView(missionId);
    });
  };
  var _addEvent2BtnUpdateMission = function(){
    $(document).on('click.juzBrad.bk.updateMission','button.btn-update-mission',function(){
      var title = $('#title').val();
      var link = $('#third_part_link').val();
      var active = null;
      if($('#mission_active').length > 0)
        active = $('#mission_active').val();
      _updateMission(title,link,active);
    });
  };
  var _addEvent2LinkRemoveMission = function(){
    $(document).on('click.juzBrad.bk.removeMission','a.removeMission',function(){
      var missionId = $(this).attr('data-missionId');
      _removeMission(missionId);
    });
  };
  var _addEvent2BtnCancelUpdateMisssion = function(){
    $(document).on('click.juzbrad.bk.cancelupdatemission','a.back-to-missions',function(){
      _loadMissions();
    });
  };

  var _addEvent2LinkLoadAddPropositionForm = function(){
    $(document).on('click.juzBrad.bk.loadAddPropositionForm','a.a-load-add-proposition-form',function(){
      _loadAddPropositionForm();
    });
  };
  var _addEvent2BtnAddProposition = function(){
    $(document).on('click.juzBrad.bk.addProposition','button.btn-add-proposition',function(){
      var formDOM = $('.uiPopup').find('.addNewProposition');
      var content = _getDataFromCkEditor();
      if(content == ""){
        content = formDOM.find('textarea').val();
      }
      var active = formDOM.find(':checkbox').val();
      _addProposition(content,active);
    })
  };
  var _addEvent2LinkLoadEditPropositionForm = function(){
    $(document).on('click.juzBrad.bk.loadEditPropositionForm','a.a-load-edit-proposition-form',function(){
      var propositionId = $(this).attr('data-propositionId');
      _loadEditPropositionForm(propositionId);
    });
  };
  var _addEvent2BtnUpdateProposition = function(){
    $(document).on('click.juzBrad.bk.updateProposition','button.btn-update-proposition',function(){
      var propositionId = $(this).attr('data-propositionId');
      var formDOM = $('.uiPopup').find('.addNewProposition');
      var content = _getDataFromCkEditor();
      if(content == ""){
        content = formDOM.find('textarea').val();
      }
      var active = formDOM.find(':checkbox').val();
      _updateProposition(propositionId,content,active);
    });
  };
  var _addEvent2LinkRemoveProposition = function(){
    $(document).on('click.juzBrad.bk.removeProposition','a.remove-proposition',function(){
      var propositionId = $(this).attr('data-propositionId');
      _removeProposition(propositionId);
    });
  };
  var _addEvent2BtnViewMissionParticipant = function(){
    $(document).on('click.juzBrad.bk.viewMP','a.mp-view',function(){
      var missionParticipantId = $(this).attr('data-mission-participant-id');
      var username = $(this).attr('data-participant-id');
      _loadMissionParticipantDetail(missionParticipantId,username);
    })
  };

  var _addEvent2BtnSearchMissionParticipant = function(){
    $(document).on('click.juzBrad.bk.searchMissionParticipant.btn','button.btn-search-mission-participant',function(){
      var searchForm = $(this).parent('.uiSearchInput');
      var keyword = searchForm.find(':text').val();
      var statusFilter = searchForm.find('select').val();
      _loadMissionParticipants(keyword,statusFilter,1);
    });
  };
  var _addEvent2InputTextKeywordSearchMissionParticipant = function(){
    $(document).on('keypress.juzBrad.bk.searchMissionParticipant.keyword','input.keyword-search-mission-participant',function(e){
      var key = e.which;
      if(key == 13)
      {
        var searchForm = $(this).parent('.uiSearchInput');
        var keyword = searchForm.find(':text').val();
        var statusFilter = searchForm.find('select').val();
        _loadMissionParticipants(keyword,statusFilter,1);
      }
    });
  };

  var _addEvent2LinkPageSearchMissionParticipant = function(){
    $(document).on('click.juzBrad.bk.searchMissionParticipant.page','li.search-mission-participant-page',function(){
      var searchForm = $(this).parent('.uiSearchInput');
      var keyword = searchForm.find(':text').val();
      var statusFilter = searchForm.find('select').val();
      var page = $(this).attr('data-page');
      _loadMissionParticipants(keyword,statusFilter,page);
    });
  };
  var _addEvent2SelectStatusSearchMissionParticipant = function(){
    $(document).on('change.juzBrad.bk.searchMissionParticipant.status','select.status-search-mission-participant',function(){
      var searchForm = $(this).parent('.uiSearchInput');
      var keyword = searchForm.find(':text').val();
      var statusFilter = searchForm.find('select').val();
      _loadMissionParticipants(keyword,statusFilter,1);
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
    _addEvent2InputUsername();
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
  };

  var _initVar = function(){
    _textAreaContentId = "bradAdvPropositionContent";
    _bodyContainerDOM = $(".tab-content");
  }
  brandAdvBackend.init = function(isAdmin){
    _initVar();
    if(isAdmin){
      _addEvent2LinkClosePopup();
      _initProgramEvent();
      _initManagerEvent();
      _initMissionEvent();
      _initPropositionEvent();
      _addEventIPhoneStyle2CheckBox();
      _addEvent2BtnIphoneCheckbox();
      _addEvent2RoleSelect();
      _addEvent2PrioritySelect();
    }else{
      _menuStyleController('participant');
    }
    _initMissionParticipantEvent();
    _addEvent2MPStatus();
  };
  brandAdvBackend.initMissionParticipant = function(mode,missionParticipantId,username){
    _initVar();
    if(mode == "mp_view"){
      _menuStyleController('participant');
      _loadMissionParticipantDetail(missionParticipantId,username);
    }else{
      _menuStyleController('participant');
      _loadMissionParticipantContainer();
    }

  };
  brandAdvBackend.initProgram = function(){
    _initVar();
    _menuStyleController('program');
    _loadProgramContentView();
  };

  return brandAdvBackend;
})($);