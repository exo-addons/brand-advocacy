/**
 * Created by exoplatform on 13/10/14.
 */
$(function() {
    function bradFrontend(){

    };
    bradFrontend.prototype.ajaxCommonRequest = function(url,data,callBackFct){

        var _this = this;
        $.ajax({
            dataType: "json",
            url: url,
            data: data,
            success: function(data){
                callBackFct(data,_this);
            }
        });
    };
    bradFrontend.prototype  .loadStart = function () {
        $(document).jzAjax("JuZFrontEndApplication.loadStartContent()",{
            data:{'ajx':1},
            success: function(data) {
                alert(data);
            }
        });
    };
    var bradFrontend = new bradFrontend();
    bradFrontend.loadStart();

});
