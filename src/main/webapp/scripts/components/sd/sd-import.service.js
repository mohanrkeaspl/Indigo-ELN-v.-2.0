angular
    .module('indigoeln')
    .factory('SdImportService', sdImportService);

/* @ngInject */
function sdImportService($http, $q, $uibModal, AppValues, Dictionary, SdConstants,
                         AlertModal, Alert, CalculationService, StoichTableCache, sdProperties, SdImportHelperService) {

    var auxPrefixes = [
        'COMPOUND_REGISTRATION_'
    ];

    return {
        importFile: importFile
    };

    function formatProperty(property, value, dicts) {
        if (SdImportHelperService.additionalFormatFunctions[property.code]) {
            return SdImportHelperService.additionalFormatFunctions[property.code](property, value);
        }
        return getWord(property.propName, property.subPropName, value, dicts);
    }

    function getWord(propName, subPropName, value, dicts) {
        var item = _.find(dicts, function (dict) {
            return dict.name === propName;
        });
        if (item) {
            return getItem(item.words, subPropName, value);
        }
    }

    function getItem(list, prop, value) {
        return _.find(list, function (item) {
            return item[prop].toUpperCase() === value.toUpperCase();
        });
    }

    function saveMolecule(mol) {
        var deferred = $q.defer();
        $http({
            url: 'api/bingodb/molecule/',
            method: 'POST',
            data: mol
        }).success(function (structureId) {
            deferred.resolve(structureId);
        });

        return deferred.promise;
    }

    function fillProperties(sdUnitToImport, itemToImport, dicts) {
        if (sdUnitToImport.properties) {
            _.each(SdConstants, function (property, propName) {
                var value = sdUnitToImport.properties[property.code];
                if (!value) {
                    value = _.chain(auxPrefixes)
                        .map(function (auxPrefix) {
                            return auxPrefix + propName;
                        })
                        .map(function (code) {
                            return sdUnitToImport.properties[code];
                        })
                        .find(function (val) {
                            return !_.isUndefined(val);
                        }).value();
                }
                if (value) {
                    value = formatProperty(property, value, dicts);
                }
                if (value) {
                    if (itemToImport[property.name]) {
                        angular.merge(itemToImport[property.name], value);
                    } else {
                        itemToImport[property.name] = value;
                    }
                }
            });
        }
    };

    function importItems(sdUnitsToImport, dicts, i, addToTable, callback, complete) {
        if (!sdUnitsToImport[i]) {
            if (complete) {
                complete();
            }
            Alert.info(sdUnitsToImport.length + ' batches successfully imported');

            return;
        }
        var sdUnitToImport = sdUnitsToImport[i];
        saveMolecule(sdUnitToImport.mol).then(function (structureId) {
            CalculationService.getImageForStructure(sdUnitToImport.mol, 'molecule', function (result) {
                var stoichTable = StoichTableCache.getStoicTable();
                var itemToImport = angular.copy(CalculationService.createBatch(stoichTable, true));
                itemToImport.structure = itemToImport.structure || {};
                itemToImport.structure.image = result;
                itemToImport.structure.structureType = 'molecule';
                itemToImport.structure.molfile = sdUnitToImport.mol;
                itemToImport.structure.structureId = structureId;

                fillProperties(sdUnitToImport, itemToImport, dicts);
                CalculationService.recalculateSalt(itemToImport).then(function () {
                    addToTable(itemToImport).then(function (batch) {
                        if (callback && _.isFunction(callback)) {
                            callback(batch);
                        }
                        importItems(sdUnitsToImport, dicts, i + 1, addToTable, callback, complete);
                    });
                });
            });
        });
    }

    function importFile(addToTable, callback, complete) {
        $uibModal.open({
            animation: true,
            size: 'lg',
            templateUrl: 'scripts/components/fileuploader/single-file-uploader/single-file-uploader-modal.html',
            controller: 'SingleFileUploaderController',
            controllerAs: 'vm',
            resolve: {
                url: function () {
                    return 'api/sd/import';
                }
            }
        }).result.then(function (result) {
            Dictionary.all({}, function (dicts) {
                importItems(result, dicts, 0, addToTable, callback, complete);
            });
        }, function () {
            complete();
            AlertModal.error('This file cannot be imported. Error occurred.');
        });
    }
}
