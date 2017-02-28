$.fn.editTable = function(submitCallback) {
    return this.each(function() {
        var rootEl = $(this);
        var cancelButton = $('<span>')
            .addClass('button')
            .addClass('tiny')
            .append(
                $('<i>')
                    .addClass('fa')
                    .addClass('fa-times')
            ).hide();

        var submitButton = $('<span>')
            .addClass('button')
            .addClass('tiny')
            .append(
                $('<i>')
                    .addClass('fa')
                    .addClass('fa-check')
            ).hide();

        var editButton = $('<span>')
            .addClass('button')
            .addClass('tiny')
            .append(
                $('<i>')
                    .addClass('fa')
                    .addClass('fa-pencil')
            );

        var actionCell = $(this).find("td[data-te-role='actions']");
        var dataCells = $(this).find("td[data-te-role='value']");

        var inputs = [];
        var cellVals = {};
        var textCells = [];

        dataCells.each(function () {
            var cell = $(this);
            var input = $("<input type='text'>").data('te-attribute', cell.data('te-attribute'));

            input.keyup(function(e) {
                if (e.keyCode == 13) {
                    submitButton.click();
                    e.preventDefault();
                    if (e.stopPropagation)
                        e.stopPropagation();
                    if (e.preventBubble)
                        e.preventBubble();
                    return false;
                }
            });

            var textCell = $("<span>")
                .addClass("te-data-holder")
                .text(cell.text())
                .data('te-attribute', cell.data('te-attribute'))
                .data('te-value', cell.data('te-value'));
            cell.text('');
            cell.append(textCell);
            cell.append(input);

            inputs.push(input);
            textCells.push(textCell);
        });

        var isPromise = function(obj) {
            return (obj != null && typeof obj.then == 'function');
        }

        var storeCellVals = function () {
            cellVals = {};
            for (var i = 0; i < textCells.length; i++)
                cellVals[textCells[i].data('te-attribute')] = textCells[i].data('te-value');
        };

        var loadCellVals = function () {
            for (var i = 0; i < textCells.length; i++) {
                var val = cellVals[textCells[i].data('te-attribute')];
                textCells[i].text(val);
                textCells[i].data('te-value', val);
            }
            for (var i = 0; i < inputs.length; i++)
                inputs[i].val(cellVals[inputs[i].data('te-attribute')]);
        };

        var setEditMode = function (editMode) {
            if (!editMode) {
                submitButton.hide();
                cancelButton.hide();
                editButton.show();
                for (var i = 0; i < inputs.length; i++) {
                    inputs[i].hide();
                }
                for (var i = 0; i < textCells.length; i++) {
                    textCells[i].show();
                }
            } else {
                submitButton.show();
                cancelButton.show();
                editButton.hide();

                for (var i = 0; i < inputs.length; i++) {
                    inputs[i].show();
                    var attr = inputs[i].data('te-attr');
                    inputs[i].val(cellVals[attr]);
                }
                for (var i = 0; i < textCells.length; i++) {
                    textCells[i].hide();
                }
                inputs[0].focus();
                setTimeout(function () {
                    inputs[0].select();
                }, 50);
            }
        }

        $(actionCell).append(submitButton);
        $(actionCell).append(cancelButton);
        $(actionCell).append(editButton);

        $(editButton).click(function (e) {
            setEditMode(true);
            loadCellVals();
        });

        $(cancelButton).click(function () {
            setEditMode(false);
            storeCellVals();
        });

        $(submitButton).click(function() {
            var newValues = {};
            for (var i = 0; i < inputs.length; i++) {
                newValues[inputs[i].data('te-attribute')] = inputs[i].val();
            }
            currentValues = $.extend({}, cellVals);
            var rowId = rootEl.data('te-row-id');
            obj = submitCallback(rowId, newValues, currentValues);

            var afterReceive = function(obj) {
                for (var k in obj) {
                    if (obj.hasOwnProperty(k)) {
                        cellVals[k] = obj[k];
                    }
                }
                loadCellVals();
                setEditMode(false);
            };

            if (isPromise(obj)) {
                obj.then(afterReceive);
            } else {
                afterReceive(obj);
            }
        });

        storeCellVals();
        loadCellVals();
        setEditMode(false);
    });
};