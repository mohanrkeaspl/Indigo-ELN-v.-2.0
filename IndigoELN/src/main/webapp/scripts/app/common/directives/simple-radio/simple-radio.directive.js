(function() {
    angular
        .module('indigoeln')
        .directive('simpleRadio', simpleRadio);

    /* @ngInject */
    function simpleRadio() {
        var lastId = 0;

        return {
            restrict: 'E',
            compile: function($element) {
                var $input = $element.find('input');
                var $label = $element.find('label');
                var id = 'simpleRadio_' + lastId;
                lastId += 1;

                $input.attr('type', 'radio');

                if (!$label.length) {
                    $element.append('<label for="' + id + '"></label>');
                } else {
                    $label.attr('for', id);
                }

                $input.attr('id', id);
            }
        };
    }
})();
