/* @ngInject */
function entityHelperService(CacheFactory, tabKeyService, confirmationModalService, notifyService) {
    var versionCache = CacheFactory.get('versionCache') || CacheFactory('versionCache');
    var isConflictConfirmOpen = false;

    return {
        checkVersion: checkVersion
    };

    function checkVersion($stateParams, data, currentEntity, entityTitle, isChanged, refreshClbk) {
        if (_.isEqual($stateParams, data.entity) && data.version > currentEntity.version) {
            versionCache.put(tabKeyService.getTabKeyFromParams($stateParams), data.version);

            if (isChanged) {
                if (isConflictConfirmOpen) {
                    return;
                }

                isConflictConfirmOpen = confirmationModalService
                    .openEntityVersionsConflictConfirm(entityTitle)
                    .then(refreshClbk, function() {
                        currentEntity.version = versionCache.get(tabKeyService.getTabKeyFromParams($stateParams));
                    })
                    .finally(function() {
                        isConflictConfirmOpen = false;
                    });

                return;
            }
            refreshClbk().then(function() {
                notifyService.info(entityTitle + ' has been changed by another user and reloaded');
            });
        }
    }
}

module.exports = entityHelperService;
