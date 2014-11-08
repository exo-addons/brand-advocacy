/**
 * Created by exoplatform on 08/10/14.
 */
(function($) {

  var _currentMPStatus;
  var brandAdvBackend = {};

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
            alert("something went wrong, cannot update manager");
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

  brandAdvBackend.loadPreviousMissionParticipant = function(username){

    $(".previous-mission-participant").jzAjax("MissionParticipantController.getPreviousMissionParticipant()",{
      data:{username:username},
      success:function(data){
        $(".previous-mission-participant").html(data);
      }
    });
  };

  brandAdvBackend.init = function(){
    _addEventIPhoneStyle2CheckBox();
    _addEvent2BtnIphoneCheckbox();
    _addEvent2RoleSelect();
    _addEvent2PrioritySelect();
    _addEvent2MPStatus();
  }

  return brandAdvBackend;
})($);