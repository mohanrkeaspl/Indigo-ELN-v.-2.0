var indigoAttachments = require('./directives/attachments/indigo-attachments.directive');
var indigoBatchStructure = require('./directives/batch-structure/indigo-batch-structure.directive');
var indigoComponents = require('./directives/indigo-components/indigo-components.directive');
var indigoPreferredCompoundDetails =
    require('./directives/prefer-compound-details/indigo-prefer-compound-details.directive');
var indigoPreferredCompoundsSummary =
    require('./directives/prefer-compounds-summary/indigo-prefer-compound-summary.directive');
var indigoProductBatchDetails = require('./directives/product-batch-details/indigo-product-batch-details.directive');
var indigoProductBatchSummary = require('./directives/product-batch-summary/indigo-product-batch-summary.directive');
var indigoReactionScheme = require('./directives/reaction-scheme/indigo-reaction-scheme.directive');
var indigoBatchSummary = require('./common/batch-summary/indigo-batch-summary.directive');
var indigoCompoundSummary = require('./common/compound-summary/indigo-compound-summary.directive');
var indigoInlineLoader = require('./common/inline-loader/indigo-inline-loader.directive');
var indigoConceptDetails = require('./directives/concept-details/indigo-concept-details.directive');
var indigoExperimentDescription =
    require('./directives/experiment-description/indigo-experiment-description.directive');
var indigoSearchResultTable = require('./common/search-result-table/indigo-search-result-table.directive');

var indigoStoichTable = require('./directives/stoich-table/stoich-table.module');
var componentButtons = require('./directives/component-buttons/component-buttons.module.js');
var structureScheme = require('./common/structure-scheme/structure-scheme.module');
var editInfoPopup = require('./common/edit-info-popup/edit-info-popup.module');
var indigoTable = require('./common/table/indigo-table.module');

var ProductBatchSummarySetSourceController =
    require('./directives/product-batch-summary-set-source/product-batch-summary-set-source.controller');
var AnalyzeRxnController = require('./common/analyze-rxn/analyze-rxn.controller');
var EntitiesToSaveController = require('./services/dialog-service/entities-to-save/entities-to-save.controller');
var StructureValidationController =
    require('./services/dialog-service/structure-validation/structure-validation.controller');
var SearchReagentsController = require('./common/search-reagents/search-reagents.controller');
var SomethingDetailsController = require('./common/something-details/something-details.controller');

var typeOfComponents = require('./constants/type-of-components.constant');
var searchReagentsConstant = require('./common/search-reagents/search-reagents.constant');

var productBatchSummaryCacheService = require('./services/product-batch-summary-cache.service');
var productBatchSummaryOperations = require('./services/product-batch-summary-operations.service');
var batchHelperService = require('./services/batch-helper.service');
var columnActionsService = require('./services/column-actions.service');
var dialogService = require('./services/dialog-service/dialog.service');
var componentsUtilService = require('./utils/components-util.service');

var run = require('./indigo-components.run');

var dependencies = [
    indigoStoichTable,
    componentButtons,
    structureScheme,
    editInfoPopup,
    indigoTable
];

module.exports = angular
    .module('indigoeln.indigoComponents', dependencies)

    .directive('indigoSearchResultTable', indigoSearchResultTable)
    .directive('indigoAttachments', indigoAttachments)
    .directive('indigoBatchStructure', indigoBatchStructure)
    .directive('indigoComponents', indigoComponents)
    .directive('indigoPreferredCompoundDetails', indigoPreferredCompoundDetails)
    .directive('indigoPreferredCompoundsSummary', indigoPreferredCompoundsSummary)
    .directive('indigoProductBatchDetails', indigoProductBatchDetails)
    .directive('indigoProductBatchSummary', indigoProductBatchSummary)
    .directive('indigoReactionScheme', indigoReactionScheme)
    .directive('indigoBatchSummary', indigoBatchSummary)
    .directive('indigoCompoundSummary', indigoCompoundSummary)
    .directive('indigoInlineLoader', indigoInlineLoader)
    .directive('indigoConceptDetails', indigoConceptDetails)
    .directive('indigoExperimentDescription', indigoExperimentDescription)

    .controller('ProductBatchSummarySetSourceController', ProductBatchSummarySetSourceController)
    .controller('AnalyzeRxnController', AnalyzeRxnController)
    .controller('EntitiesToSaveController', EntitiesToSaveController)
    .controller('StructureValidationController', StructureValidationController)
    .controller('SearchReagentsController', SearchReagentsController)
    .controller('SomethingDetailsController', SomethingDetailsController)

    .constant('typeOfComponents', typeOfComponents)
    .constant('searchReagentsConstant', searchReagentsConstant)

    .factory('productBatchSummaryCacheService', productBatchSummaryCacheService)
    .factory('productBatchSummaryOperations', productBatchSummaryOperations)
    .factory('batchHelperService', batchHelperService)
    .factory('columnActionsService', columnActionsService)
    .factory('dialogService', dialogService)
    .factory('componentsUtilService', componentsUtilService)

    .run(run)

    .name;
