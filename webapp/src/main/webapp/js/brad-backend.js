/**
 * Created by exoplatform on 08/10/14.
 */
(function($) {

  var _currentMPStatus;
  var _bodyContainerDOM;
  var _managerContainerDOM;
  var propositionContainerDOM;
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
          _messageConfirmCBController('success','program has been saved');
          return;
        }
        _messageConfirmCBController('unsuccessful','');
      }
    });
  }

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

  var _loadProgramManagers = function(){
    $('.jz').jzAjax('ManagerController.listProgramManagers()',{
      success:function(data){
        if(data !== 'nok'){
          _managerContainerDOM.html(data);
          _addEventIPhoneStyle2CheckBox();
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

  var _addEvent2BtnRemoveProgramManager = function(){
    $(document).on('click.juzBrad.bk.removeprogramuser','a.removeProgramUser',function(){
      var username = $(this).attr("data-username");
      _removeProgramManager(username);
    });
  };

  var _addProgramManager = function(username,role,notif){
    $('.jz').jzAjax('ManagerController.add2Program()',{
      data:{username:username,role:role,notif:notif},
      success:function(data){
        if(data !== 'nok'){
          _loadProgramManagers();
        }
      }
    });
  };
  var _addEvent2BtnAddProgramManager = function(){
    $(document).on('click.juzBrad.bk.addprogramuser','button.btn-add-program-manager',function(){
      var username = $(".manager-username").val();
      var role = $(".manager-role").val();
      var notif = $(".manager-notif").val();
      _addProgramManager(username,role,notif);
    });
  };

  var _loadMissions = function(){
    $('.jz').jzAjax("MissionController.index()",{
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
        }
      }
    });
  };
  var _addEvent2BtnLoadEditMissionForm = function(){
    $(document).on('click.juzBrad.bk.editmission','a.load-edit-mission-form',function(){
      var missionId = $(this).attr('data-missionId');
      _loadEditMissionFormView(missionId);
    });
  };

  var _loadAddMissionFormView = function(){
    $('.jz').jzAjax('MissionController.addForm()',{
      success:function(data){
        _bodyContainerDOM.html(data);
      }
    });
  };
  var _addEvent2BtnLoadAddMissionForm = function(){
    $(document).on('click.juzBrad.bk.loadAddMissionFormView','button.btn-load-add-mission-form',function(){
      _loadAddMissionFormView();
    });
  };

  var _addMission = function(title,link,priority){
    $('.jz').jzAjax('MissionController.addMission()',{
      data:{title:title,link:link,priority:priority},
      success:function(data){
        if(data === 'nok'){
          _messageConfirmCBController('unsuccessful','cannot create mission')
          return;
        }
        _loadMissions();
      }
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

  var _updateMission = function(id,title,link,priority,active){
    $('.jz').jzAjax('MissionController.updateMission()',{
      data:{id:id,title:title,link:link,active:active},
      success:function(data){
        if(data === 'nok'){
          _messageConfirmCBController('unsuccessful','cannot update mission');
        }
        else{
          _messageConfirmCBController('success','Mission has been updated');
        }
      }
    });
  };
  var _addEvent2BtnUpdateMission = function(){
    $(document).on('click.juzBrad.bk.updateMission','button.btn-update-mission',function(){
      var id = $(this).attr('data-missionId');
      var title = $('#title').val();
      var link = $('#third_part_link').val();
      var priority = $('#priority').val();
      var active = $('#active').val();
      _updateMission(id,title,link,active);
    });
  };

  var _loadPropositions = function(missionId){

  };
  var _loadMissionParticipants = function(keyword,status,page){

  };
  var _loadMissionParticipantDetail = function(missionParticipantId){

  };
  var _loadAddPropositionForm = function(missionId){

  };
  var _loadEditPropositionForm = function(propositionId){

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
              alert("something went wrong, cannot update manager");
            }
          }
        });
      } else if(action == "updateMissionInline"){
        var missionId = input.attr("data-missionId");
        var active = val;
        $(input).jzAjax("MissionController.ajaxUpdateInline()",{
          data:{missionId:missionId,action:"active",val:active},
          success:function(data){
            try{
              var obj = data = $.parseJSON(data);
              if($(".mission-prio-exceeded"))
                $(".mission-prio-exceeded").hide();
              if (obj.error){
                if(obj.msg=="mission-prio-exceeded"){
                  $(".mission-prio-exceeded").show();
                }else
                  alert(obj.msg);
              }
            }catch (e){}
          }
        });
      } else if (action == "updatePropositionInline"){
        var propositionId = input.attr("data-propositionId");
        var propositionActive = val;
        $(input).jzAjax("PropositionController.ajaxUpdatePropositionInline()",{
          data:{propositionId:propositionId,action:"active",val:propositionActive},
          success:function(data){
            if (data == "nok"){
              alert("something went wrong, cannot update proposition");
            }
          }
        });
      } else if(action == "addNewProgramManager"){

      }

    });
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
            _messageConfirmCBController('unsuccess','something went wrong, cannot update manager');
          }else{
            _messageConfirmCBController('success','Manager role has been updated');
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
      jPriority.jzAjax("MissionController.ajaxUpdateInline()",{
        data:{missionId:missionId,action:"priority",val:priority},
        success:function(data){
          try{
            var obj = data = $.parseJSON(data);
            if($(".mission-prio-exceeded"))
              $(".mission-prio-exceeded").hide();
            if (obj.error){
              if(obj.msg=="mission-prio-exceeded"){
                $(".mission-prio-exceeded").show();
              }else
                alert(obj.msg);
            }
          }catch (e){}
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
              alert(data);
              jStatus.val(_currentMPStatus);
            }
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
        $(".jz").jzAjax('ManagerController.searchEXOUsers()',{
          data:{keyword:keyword},
          success:function(data){
            $(".result-search-manager").html(data);
          }
        });
      }
    })
  };

  var _addEvent2ProgramTabMenu = function(){
    _menuStyleController('program');
    $(document).on('click.juzBrad.bk.program.tabmenu','li.program-tab',function(){
      _loadProgramContentView();
    });
  };

  var _addEvent2MissionTabMenu = function(){
    _menuStyleController('mission');
    $(document).on('click.juzBrad.bk.mission.tabmenu','li.mission-tab',function(){
      _loadMissions();
    });
  };



  brandAdvBackend.loadPreviousMissionParticipant = function(username){

    $(".previous-mission-participant").jzAjax("MissionParticipantController.getPreviousMissionParticipant()",{
      data:{username:username},
      success:function(data){
        $(".previous-mission-participant").html(data);
      }
    });
  };

  var _test = function(){
    alert('test');
  }

  brandAdvBackend.init = function(){
    _bodyContainerDOM = $(".tab-content");
    _loadProgramContentView();
    _addEvent2ProgramTabMenu();
    _addEvent2MissionTabMenu();
    _addEvent2BtnAddProgram();
    _addEvent2BtnUpdateProgram();
    _addEvent2BtnAddProgramManager();
    _addEvent2BtnRemoveProgramManager();
    _addEvent2InputUsername();
    _addEventIPhoneStyle2CheckBox();
    _addEvent2BtnIphoneCheckbox();
    _addEvent2RoleSelect();
    _addEvent2PrioritySelect();
    _addEvent2MPStatus();
  }

  return brandAdvBackend;
})($);