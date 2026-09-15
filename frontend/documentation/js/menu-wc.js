'use strict';

customElements.define('compodoc-menu', class extends HTMLElement {
    constructor() {
        super();
        this.isNormalMode = this.getAttribute('mode') === 'normal';
    }

    connectedCallback() {
        this.render(this.isNormalMode);
    }

    render(isNormalMode) {
        let tp = lithtml.html(`
        <nav>
            <ul class="list">
                <li class="title">
                    <a href="index.html" data-type="index-link">SIGEA-GTT Frontend Documentation</a>
                </li>

                <li class="divider"></li>
                ${ isNormalMode ? `<div id="book-search-input" role="search">
    <input type="text" placeholder="Type to search">
    <button type="button"
        class="search-input-clear"
        aria-label="Clear search"
        data-search-input-clear>&times;</button>
</div>
` : '' }
                <li class="chapter">
                    <a data-type="chapter-link" href="index.html"><span class="icon ion-ios-home"></span>Getting started</a>
                    <ul class="links">
                                <li class="link">
                                    <a href="overview.html" data-type="chapter-link">
                                        <span class="icon ion-ios-keypad"></span>Overview
                                    </a>
                                </li>

                            <li class="link">
                                <a href="index.html" data-type="chapter-link">
                                    <span class="icon ion-ios-paper"></span>
                                        README
                                </a>
                            </li>
                                <li class="link">
                                    <a href="architecture.html" data-type="chapter-link">
                                        <span class="icon ion-ios-git-branch"></span>Architecture
                                    </a>
                                </li>
                                <li class="link">
                                    <a href="dependencies.html" data-type="chapter-link">
                                        <span class="icon ion-ios-list"></span>Dependencies
                                    </a>
                                </li>
                                <li class="link">
                                    <a href="properties.html" data-type="chapter-link">
                                        <span class="icon ion-ios-apps"></span>Properties
                                    </a>
                                </li>

                    </ul>
                </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-bs-toggle="collapse" ${ isNormalMode ? 'data-bs-target="#components-links"' :
                            'data-bs-target="#xs-components-links"' }>
                            <span class="icon ion-md-cog"></span>
                            <span>Components</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse " ${ isNormalMode ? 'id="components-links"' : 'id="xs-components-links"' }>
                            <li class="link">
                                <a href="components/ActivityFormComponent.html" data-type="entity-link" >ActivityFormComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ActivityGradingDetailComponent.html" data-type="entity-link" >ActivityGradingDetailComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ActivityGradingListComponent.html" data-type="entity-link" >ActivityGradingListComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ActivityResolutionComponent.html" data-type="entity-link" >ActivityResolutionComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/AppComponent.html" data-type="entity-link" >AppComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/AppShellComponent.html" data-type="entity-link" >AppShellComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ClassDashboardComponent.html" data-type="entity-link" >ClassDashboardComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ClassDetailComponent.html" data-type="entity-link" >ClassDetailComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ClassFormComponent.html" data-type="entity-link" >ClassFormComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ClassListComponent.html" data-type="entity-link" >ClassListComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ConfirmDialogComponent.html" data-type="entity-link" >ConfirmDialogComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/DataTablePaginationComponent.html" data-type="entity-link" >DataTablePaginationComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FirstLoginComponent.html" data-type="entity-link" >FirstLoginComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/LoadingSpinnerComponent.html" data-type="entity-link" >LoadingSpinnerComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/LoginComponent.html" data-type="entity-link" >LoginComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ModuleCatalogComponent.html" data-type="entity-link" >ModuleCatalogComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ModuleFormComponent.html" data-type="entity-link" >ModuleFormComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ModuleListComponent.html" data-type="entity-link" >ModuleListComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ProfileComponent.html" data-type="entity-link" >ProfileComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/SeverityFormComponent.html" data-type="entity-link" >SeverityFormComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/SeverityGuideComponent.html" data-type="entity-link" >SeverityGuideComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/SeverityListComponent.html" data-type="entity-link" >SeverityListComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/StudentActivitiesComponent.html" data-type="entity-link" >StudentActivitiesComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/SubmissionFeedbackComponent.html" data-type="entity-link" >SubmissionFeedbackComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ToastContainerComponent.html" data-type="entity-link" >ToastContainerComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/TriggerFormComponent.html" data-type="entity-link" >TriggerFormComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/TriggerGuideComponent.html" data-type="entity-link" >TriggerGuideComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/TriggerListComponent.html" data-type="entity-link" >TriggerListComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/UserFormComponent.html" data-type="entity-link" >UserFormComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/UserListComponent.html" data-type="entity-link" >UserListComponent</a>
                            </li>
                        </ul>
                    </li>
                        <li class="chapter">
                            <div class="simple menu-toggler" data-bs-toggle="collapse" ${ isNormalMode ? 'data-bs-target="#injectables-links"' :
                                'data-bs-target="#xs-injectables-links"' }>
                                <span class="icon ion-md-arrow-round-down"></span>
                                <span>Injectables</span>
                                <span class="icon ion-ios-arrow-down"></span>
                            </div>
                            <ul class="links collapse " ${ isNormalMode ? 'id="injectables-links"' : 'id="xs-injectables-links"' }>
                                <li class="link">
                                    <a href="injectables/AcademicClassService.html" data-type="entity-link" >AcademicClassService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/ActivityService.html" data-type="entity-link" >ActivityService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/AuthService.html" data-type="entity-link" >AuthService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/ClassDashboardService.html" data-type="entity-link" >ClassDashboardService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/GttModuleService.html" data-type="entity-link" >GttModuleService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/GttTriggerService.html" data-type="entity-link" >GttTriggerService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/HarmSeverityService.html" data-type="entity-link" >HarmSeverityService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/LoadingService.html" data-type="entity-link" >LoadingService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/ToastService.html" data-type="entity-link" >ToastService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/UserService.html" data-type="entity-link" >UserService</a>
                                </li>
                            </ul>
                        </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-bs-toggle="collapse" ${ isNormalMode ? 'data-bs-target="#guards-links"' :
                            'data-bs-target="#xs-guards-links"' }>
                            <span class="icon ion-ios-lock"></span>
                            <span>Guards</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse " ${ isNormalMode ? 'id="guards-links"' : 'id="xs-guards-links"' }>
                            <li class="link">
                                <a href="guards/authGuard.html" data-type="entity-link" >authGuard</a>
                            </li>
                            <li class="link">
                                <a href="guards/firstLoginGuard.html" data-type="entity-link" >firstLoginGuard</a>
                            </li>
                            <li class="link">
                                <a href="guards/roleGuard.html" data-type="entity-link" >roleGuard</a>
                            </li>
                        </ul>
                    </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-bs-toggle="collapse" ${ isNormalMode ? 'data-bs-target="#interfaces-links"' :
                            'data-bs-target="#xs-interfaces-links"' }>
                            <span class="icon ion-md-information-circle-outline"></span>
                            <span>Interfaces</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse " ${ isNormalMode ? ' id="interfaces-links"' : 'id="xs-interfaces-links"' }>
                            <li class="link">
                                <a href="interfaces/AcademicClassCloseDTO.html" data-type="entity-link" >AcademicClassCloseDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/AcademicClassCreateDTO.html" data-type="entity-link" >AcademicClassCreateDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/AcademicClassDetailDTO.html" data-type="entity-link" >AcademicClassDetailDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/AcademicClassResponseDTO.html" data-type="entity-link" >AcademicClassResponseDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/AcademicClassUpdateDTO.html" data-type="entity-link" >AcademicClassUpdateDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ActivityCreateDTO.html" data-type="entity-link" >ActivityCreateDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ActivityDetailDTO.html" data-type="entity-link" >ActivityDetailDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ActivityResponseDTO.html" data-type="entity-link" >ActivityResponseDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ActivityUpdateDTO.html" data-type="entity-link" >ActivityUpdateDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ApiResponse.html" data-type="entity-link" >ApiResponse&lt;T&gt;</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ChangePasswordRequest.html" data-type="entity-link" >ChangePasswordRequest</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ClassDashboardDTO.html" data-type="entity-link" >ClassDashboardDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ClassStudentSummaryDTO.html" data-type="entity-link" >ClassStudentSummaryDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ClinicalCaseData.html" data-type="entity-link" >ClinicalCaseData</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ErrorResponse.html" data-type="entity-link" >ErrorResponse</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/EvolutionNoteData.html" data-type="entity-link" >EvolutionNoteData</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/FieldErrorDetail.html" data-type="entity-link" >FieldErrorDetail</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/FirstLoginChangePasswordRequest.html" data-type="entity-link" >FirstLoginChangePasswordRequest</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/FiveWTwoHItemData.html" data-type="entity-link" >FiveWTwoHItemData</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/GttMetricsDTO.html" data-type="entity-link" >GttMetricsDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/GttModule.html" data-type="entity-link" >GttModule</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/GttModuleCreateRequest.html" data-type="entity-link" >GttModuleCreateRequest</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/GttModuleStatusUpdateRequest.html" data-type="entity-link" >GttModuleStatusUpdateRequest</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/GttModuleUpdateRequest.html" data-type="entity-link" >GttModuleUpdateRequest</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/GttTrigger.html" data-type="entity-link" >GttTrigger</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/GttTriggerCreateRequest.html" data-type="entity-link" >GttTriggerCreateRequest</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/GttTriggerStatusUpdateRequest.html" data-type="entity-link" >GttTriggerStatusUpdateRequest</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/GttTriggerUpdateRequest.html" data-type="entity-link" >GttTriggerUpdateRequest</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/GutItemData.html" data-type="entity-link" >GutItemData</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/HarmSeverity.html" data-type="entity-link" >HarmSeverity</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/HarmSeverityCreateRequest.html" data-type="entity-link" >HarmSeverityCreateRequest</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/HarmSeverityStatusUpdateRequest.html" data-type="entity-link" >HarmSeverityStatusUpdateRequest</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/HarmSeverityUpdateRequest.html" data-type="entity-link" >HarmSeverityUpdateRequest</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/IdentifiedTriggerData.html" data-type="entity-link" >IdentifiedTriggerData</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/IshikawaData.html" data-type="entity-link" >IshikawaData</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/LabExamData.html" data-type="entity-link" >LabExamData</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/LoginRequest.html" data-type="entity-link" >LoginRequest</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/LoginResponse.html" data-type="entity-link" >LoginResponse</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/PageResponse.html" data-type="entity-link" >PageResponse&lt;T&gt;</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/PdcaData.html" data-type="entity-link" >PdcaData</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/PedagogicalMetricsDTO.html" data-type="entity-link" >PedagogicalMetricsDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/PrescriptionData.html" data-type="entity-link" >PrescriptionData</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ProcedureData.html" data-type="entity-link" >ProcedureData</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/QualityToolsData.html" data-type="entity-link" >QualityToolsData</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/SubmissionCreateDTO.html" data-type="entity-link" >SubmissionCreateDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/SubmissionGradeDTO.html" data-type="entity-link" >SubmissionGradeDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/SubmissionResponseDTO.html" data-type="entity-link" >SubmissionResponseDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/SwotData.html" data-type="entity-link" >SwotData</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/Toast.html" data-type="entity-link" >Toast</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/TriggerOccurrenceDTO.html" data-type="entity-link" >TriggerOccurrenceDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/User.html" data-type="entity-link" >User</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/UserCreateRequest.html" data-type="entity-link" >UserCreateRequest</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/UserCreateResponse.html" data-type="entity-link" >UserCreateResponse</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/UserProfileUpdateRequest.html" data-type="entity-link" >UserProfileUpdateRequest</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/UserStatusUpdateRequest.html" data-type="entity-link" >UserStatusUpdateRequest</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/UserUpdateRequest.html" data-type="entity-link" >UserUpdateRequest</a>
                            </li>
                        </ul>
                    </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-bs-toggle="collapse" ${ isNormalMode ? 'data-bs-target="#miscellaneous-links"'
                            : 'data-bs-target="#xs-miscellaneous-links"' }>
                            <span class="icon ion-ios-cube"></span>
                            <span>Miscellaneous</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse " ${ isNormalMode ? 'id="miscellaneous-links"' : 'id="xs-miscellaneous-links"' }>
                            <li class="link">
                                <a href="miscellaneous/functions.html" data-type="entity-link">Functions</a>
                            </li>
                            <li class="link">
                                <a href="miscellaneous/typealiases.html" data-type="entity-link">Type aliases</a>
                            </li>
                            <li class="link">
                                <a href="miscellaneous/variables.html" data-type="entity-link">Variables</a>
                            </li>
                        </ul>
                    </li>
                        <li class="chapter">
                            <a data-type="chapter-link" href="routes.html"><span class="icon ion-ios-git-branch"></span>Routes</a>
                        </li>
                    <li class="chapter">
                        <a data-type="chapter-link" href="coverage.html"><span class="icon ion-ios-stats"></span>Documentation coverage</a>
                    </li>
                    <li class="divider"></li>
                    <li class="copyright">
                        Documentation generated using <a href="https://compodoc.app/" target="_blank" rel="noopener noreferrer">
                            <img data-src="images/compodoc-vectorise.png" class="img-responsive" data-type="compodoc-logo">
                        </a>
                    </li>
            </ul>
        </nav>
        `);
        this.innerHTML = tp.strings;
    }
});
