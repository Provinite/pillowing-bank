function isEmpty(obj) {
    for(var key in obj) {
        if(obj.hasOwnProperty(key))
            return false;
    }
    return true;
}

(function($) {
    $(document).ready(function() {
        $('input[data-focus="true"]').focus();
        $(document).foundation();
    });
})(jQuery);
