setupTooltips();

function setupTooltips() {
    $('.with-popover').on('shown.bs.popover', () => {
        $('.btn-close').on('click', () => $('.with-popover').popover('hide'));
    });

    const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
    const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));
    const popoverTriggerList = document.querySelectorAll('[data-bs-toggle="popover"]');
    const popoverList = [...popoverTriggerList].map(popoverTriggerEl => new bootstrap.Popover(popoverTriggerEl));
}

function showAddProjectPage(newBtn) {
    if (authUser != null) {
        window.location.href = '/projects/add';
    } else {
        $('.with-popover').popover('hide');
        newBtn.popover('toggle');
    }
}

let profilesCurrentPage = 0;
const profilesArea = $('#profilesArea');
const loadMoreProfilesBtn = $('#loadMoreProfilesBtn');

let projectsCurrentPage = 0;
const projectsArea = $('#projectsArea');
const loadMoreProjectsBtn = $('#loadMoreProjectsBtn');

let tagsCurrentPage = 0;
const tagsArea = $('#tagsArea');
const loadMoreTagsBtn = $('#loadMoreTagsBtn');

function loadMoreProfiles() {
    loadMoreProfilesBtn.attr('hidden', true);
    profilesCurrentPage++;
    let keyword = new URLSearchParams(window.location.search).get('keyword');

    $.ajax({
        url: '/profile/by-keyword',
        data: { keyword: keyword, page: profilesCurrentPage, size: 9 }
    }).done(profilesPage => {
        if (profilesPage.content.length !== 0) {
            profilesPage.content.forEach(profile => {
                profilesArea.append(generateProfileDiv(profile));
            });
            if (profilesPage.total !== $('.profile-div').length) {
                loadMoreProfilesBtn.attr('hidden', false);
            }
        }
    }).fail(function(data) {
        handleError(data, getMessage('user.failed-to-get-profiles'));
        profilesCurrentPage--;
        loadMoreProfilesBtn.attr('hidden', false);
    });
}

function generateProfileDiv(profile) {
    let dFlexDiv = $('<div></div>').addClass('d-flex profile-div mb-3');
    let avatarProfileLink = $('<a></a>').addClass('text-decoration-none link-body-emphasis')
        .attr('href', `/profile/${profile.id}/view`);
    let avatar = $('<img>').addClass('rounded-circle border')
        .attr('src', getAvatarLink(profile.avatar)).attr('width', '40').attr('height', '40')
        .attr('title', profile.name)
        .css('object-fit', 'cover')
        .on('mouseenter', function() {$(this).addClass('opacity-75')})
        .on('mouseleave', function() {$(this).removeClass('opacity-75')});
    avatarProfileLink.append(avatar);
    dFlexDiv.append(avatarProfileLink);
    let nameDiv = $('<div></div>').addClass('ms-2');
    let nameProfileLink = $('<a></a>').addClass('text-decoration-none link-body-emphasis')
        .attr('href', `/profile/${profile.id}/view`);
    let nameSpan = $('<span></span>').addClass('h6').html(`${profile.name}`);
    nameProfileLink.append(nameSpan);
    nameDiv.append(nameProfileLink);
    dFlexDiv.append(nameDiv);
    return dFlexDiv;
}

function getAvatarLink(avatar) {
    return avatar != null ? (avatar.fileLink.startsWith('https://') ? avatar.fileLink : `/${avatar.fileLink}`) : '/images/no-avatar.svg';
}

function loadMoreProjects() {
    loadMoreProjectsBtn.attr('hidden', true);
    projectsCurrentPage++;
    let keyword = new URLSearchParams(window.location.search).get('keyword');

    $.ajax({
        url: '/projects/by-keyword',
        data: { keyword: keyword, page: projectsCurrentPage, size: 9 }
    }).done(projectsPage => {
        if (projectsPage.content.length !== 0) {
            projectsPage.content.forEach(project => {
                projectsArea.append(generateProjectCard(project));
            });
            $('.with-popover').on('shown.bs.popover', () => {
                $('.btn-close').on('click', () => $('.with-popover').popover('hide'));
            });
            const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
            const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));
            const popoverTriggerList = document.querySelectorAll('[data-bs-toggle="popover"]');
            const popoverList = [...popoverTriggerList].map(popoverTriggerEl => new bootstrap.Popover(popoverTriggerEl));

            if (projectsPage.total !== $('.project-card').length) {
                loadMoreProjectsBtn.attr('hidden', false);
            }
        }
    }).fail(function(data) {
        handleError(data, getMessage('project.failed-to-get-projects'));
        projectsCurrentPage--;
        loadMoreProjectsBtn.attr('hidden', false);
    });
}

function generateProjectCard(project) {
    let card = $('<div></div>').addClass('card project-card rounded-3 my-3');
    let previewDiv = $('<div></div>').addClass('ratio').css('--bs-aspect-ratio', '50%');
    let preview = $('<img>').addClass('card-img-top rounded-top-3').css('object-fit', 'cover')
        .attr('src', `/${project.preview.fileLink}`);
    previewDiv.append(preview);
    card.append(previewDiv);
    let architectureDiv = $('<div></div>').attr('title', getMessage('architecture'));
    let architectureImage = $('<img>').addClass('float-end bg-light-subtle border border-light-subtle rounded-circle p-1')
        .attr('src', `/${project.architecture.logo.fileLink}`)
        .attr('data-bs-toggle', 'tooltip').attr('title', project.architecture.localizedFields.length !== 0 ?
            project.architecture.localizedFields[0].name : project.architecture.name)
        .attr('width', '40').attr('height', '40').css('margin-top', '-20px').css('margin-right', '15px')
        .css('z-index', '4').css('position', 'relative')
        .css('box-shadow', '0 1px 2px 0 rgba(0, 0, 0, 0.2), 0 1px 2px 0 rgba(0, 0, 0, 0.19)')
        .on('mouseenter', function () {
            $(this).removeClass('p-1');
        })
        .on('mouseleave', function () {
            $(this).addClass('p-1');
        });
    architectureDiv.append(architectureImage);
    card.append(architectureDiv);

    let cardBody = $('<div></div>').addClass('card-body d-flex flex-column pb-0').css('margin-top', '-35px');
    let dFlexDiv = $('<div></div>').addClass('d-flex');
    let avatarDiv = $('<div></div>').addClass('pt-3 pb-2 ps-3').css('position', 'relative').css('z-index', '2')
        .css('margin-left', '-16px');
    let avatarLink = $('<a></a>').addClass('text-decoration-none link-body-emphasis')
        .attr('href', `/profile/${project.author.id}/view`).attr('title', project.author.name);
    let avatar = $('<img>').addClass('rounded-circle border')
        .attr('src', getAvatarLink(project.author.avatar))
        .attr('width', '40').attr('height', '40').css('object-fit', 'cover')
        .on('mouseenter', function () {
            $(this).addClass('opacity-75');
        })
        .on('mouseleave', function () {
            $(this).removeClass('opacity-75');
        });
    avatarLink.append(avatar);
    avatarDiv.append(avatarLink);
    dFlexDiv.append(avatarDiv);
    let authorNameAndCreatedDiv = $('<div></div>').addClass('pt-3 pb-2 px-3').css('position', 'relative')
        .css('z-index', '2').css('margin-left', '-8px');
    let authorName = $('<span></span>').addClass('h6').text(project.author.name);
    let authorNameLink = $('<a></a>').addClass('text-decoration-none link-body-emphasis')
        .attr('href', `/profile/${project.author.id}/view`);
    authorNameLink.append(authorName);
    authorNameAndCreatedDiv.append(authorNameLink);
    let createdDiv = $('<div></div>').addClass('tiny text-secondary-emphasis').css('margin-top', '-3px')
        .text(formatDateTime(project.created));
    authorNameAndCreatedDiv.append(createdDiv);
    dFlexDiv.append(authorNameAndCreatedDiv);

    if (authUser !== null && project.author.id === authUser.id) {
        let manageDiv = $('<div></div>').addClass('pt-4 ms-auto ps-3 pe-3').css('position', 'relative')
            .css('z-index', '3').css('margin-right', '-16px');
        let manageBtn = $('<button></button>').attr('type', 'button').attr('title', getMessage('project.manage'))
            .addClass('btn btn-link link-secondary link-underline-opacity-0 p-0 dropdown-toggle manage-dropdown')
            .attr('data-bs-toggle', 'dropdown').attr('data-bs-auto-close', 'outside').attr('id', `manageBtn-${project.id}`);
        let manageIcon = $('<i></i>').addClass('fa-solid fa-ellipsis');
        manageBtn.append(manageIcon);
        manageDiv.append(manageBtn);
        let dropdownMenu = $('<ul></ul>').addClass('dropdown-menu');
        let showProjectDataItem = $('<li></li>');
        let showProjectDataLink = $('<a></a>').attr('type', 'button').addClass('dropdown-item')
            .attr('href', `/projects/${project.id}/data`)
            .html(`<i class="fa-solid fa-magnifying-glass fa-fw text-secondary me-2"></i>${getMessage('project.show')}`);
        showProjectDataItem.append(showProjectDataLink);
        dropdownMenu.append(showProjectDataItem);
        let editProjectItem = $('<li></li>');
        let editProjectLink = $('<a></a>').attr('type', 'button').addClass('dropdown-item')
            .attr('href', `/projects/edit/${project.id}`)
            .html(`<i class="fa-solid fa-pen-to-square fa-fw text-success me-2"></i>${getMessage('edit')}`);
        editProjectItem.append(editProjectLink);
        dropdownMenu.append(editProjectItem);
        let revealProjectItem = $('<li></li>');
        let revealProjectBtn = $('<button></button>').addClass('dropdown-item')
            .html(`<i class="fa-solid ${project.visible ? 'fa-eye-slash' : 'fa-eye'} fa-fw text-warning me-2"></i>${getMessage(project.visible ? 'project.hide' : 'project.reveal')}`)
            .on('click', (event) => {
                revealProject(project.id, project.name, $(event.target));
            });
        revealProjectItem.append(revealProjectBtn);
        dropdownMenu.append(revealProjectItem);
        let deleteProjectItem = $('<li></li>');
        let deleteProjectBtn = $('<button></button>').addClass('dropdown-item')
            .html(`<i class="fa-solid fa-trash-can fa-fw text-danger me-2"></i>${getMessage('delete')}`)
            .attr('tabindex', '0').attr('data-bs-toggle', 'popover').attr('data-bs-trigger', 'focus ')
            .attr('data-bs-title', `${getMessage('project.delete')}?`)
            .attr('data-bs-content', `"<div class='text-center'><a type='button' class='btn btn-sm btn-secondary me-2'>${getMessage('cancel')}</a><a type='button' id='delProject-${project.id}' class='btn btn-sm btn-danger'>${getMessage('delete')}</a></div>"`)
            .attr('data-bs-html', 'true');
        deleteProjectBtn.on('shown.bs.popover', () => {
            $(`#delProject-${project.id}`).on('click', () => deleteProject(project.id, project.name));
        });
        deleteProjectItem.append(deleteProjectBtn);
        dropdownMenu.append(deleteProjectItem);
        manageDiv.append(dropdownMenu);
        dFlexDiv.append(manageDiv);
    }

    cardBody.append(dFlexDiv);

    let name = $('<h5></h5>').addClass('card-title').text(project.name).attr('id', `${project.id}-name-elem`);
    cardBody.append(name);
    if (!project.visible) {
        let invisibleSymbol = $('<i></i>').addClass('fa-solid fa-eye-slash text-warning tiny float-end')
            .attr('title', getMessage('project.invisible-to-users')).css('position', 'relative')
            .css('z-index', '2');
        name.append(invisibleSymbol);
    }

    let annotation = $('<span></span>').addClass('card-text').text(project.annotation);
    cardBody.append(annotation);

    let likesCommentsShareViewsRow = $('<div></div>').addClass('row mt-auto pt-3 pb-1').css('position', 'relative')
        .css('z-index', '2');
    let likesCommentsShareCol = $('<div></div>').addClass('col-8');
    likesCommentsShareViewsRow.append(likesCommentsShareCol);
    let commentsBtn = $('<a></a>').addClass('btn-link text-decoration-none link-info').attr('type', 'button')
        .attr('title', getMessage('comment.comments')).attr('href', `/projects/${project.id}/view#comments`);
    let commentsSymbol = $('<i></i>').addClass('fa-regular fa-comments');
    let commentsCounter = $('<span></span>').addClass('ms-1 text-secondary-emphasis small').text(project.commentsCount);
    commentsBtn.append(commentsSymbol);
    commentsBtn.append(commentsCounter);
    likesCommentsShareCol.append(commentsBtn);
    let likeBtn = $('<a></a>').addClass('with-popover btn-link link-danger text-decoration-none ms-3')
        .attr('type', 'button').attr('data-bs-toggle', 'popover')
        .attr('data-bs-trigger', 'manual')
        .attr('data-bs-title', `"<a type='button' class='btn-close ms-2 float-end tiny'></a><div>${getMessage('info.only-for-auth-users')}</div>"`)
        .attr('data-bs-content', `"<div class='text-center'><a href='/login' type='button' class='btn btn-sm btn-warning px-3'>${getMessage('login')}</a></div>"`)
        .attr('data-bs-html', 'true')
        .on('click', function () {
            likeProject($(this), project.id);
        });
    let likeSymbol = $('<i></i>').addClass('fa-heart')
        .addClass(`${authUser === null || !project.likesUserIds.includes(authUser.id) ? 'fa-regular' : 'fa-solid'}`)
        .attr('title', getMessage('like'));
    let likeCounter = $('<span></span>').addClass('ms-1 text-secondary-emphasis small')
        .attr('title', getMessage('like')).text(project.likesUserIds.length);
    likeBtn.append(likeSymbol);
    likeBtn.append(likeCounter);
    likesCommentsShareCol.append(likeBtn);
    let shareBtn = $('<a></a>').attr('type', 'button').addClass('btn-link link-primary text-decoration-none ms-3')
        .attr('title', getMessage('project.share')).attr('data-bs-toggle', 'dropdown');
    let shareSymbol = $('<i></i>').addClass('fa-solid fa-share');
    shareBtn.append(shareSymbol);
    let shareDropDownMenu = $('<ul></ul>').addClass('dropdown-menu');
    let copyLinkItem = $('<li></li>');
    let copyLinkBtn = $('<button></button>').attr('type', 'button').addClass('dropdown-item')
        .html(`<i class="fa-solid fa-link fa-fw me-2"></i>${getMessage('project.copy-link')}`)
        .on('click', () => copyLink(project.id));
    copyLinkItem.append(copyLinkBtn);
    shareDropDownMenu.append(copyLinkItem);
    let shareOnVkItem = $('<li></li>');
    let shareOnVkBtn = $('<button></button>').attr('type', 'button').addClass('dropdown-item')
        .attr('data-id', project.id).attr('data-name', project.name)
        .html(`<i class="fa-brands fa-vk fa-fw me-2"></i>${getMessage('project.share-on-vk')}`)
        .on('click', function () {shareOnVk($(this))});
    shareOnVkItem.append(shareOnVkBtn);
    shareDropDownMenu.append(shareOnVkItem);
    let shareOnTelegramItem = $('<li></li>');
    let shareOnTelegramBtn = $('<button></button>').attr('type', 'button').addClass('dropdown-item')
        .attr('data-id', project.id).attr('data-name', project.name)
        .html(`<i class="fa-brands fa-telegram fa-fw me-2"></i>${getMessage('project.share-on-telegram')}`)
        .on('click', function () {shareOnTelegram($(this))});
    shareOnTelegramItem.append(shareOnTelegramBtn);
    shareDropDownMenu.append(shareOnTelegramItem);
    let shareOnWhatsAppItem = $('<li></li>');
    let shareOnWhatsAppBtn = $('<button></button>').attr('type', 'button').addClass('dropdown-item')
        .attr('data-id', project.id).attr('data-name', project.name)
        .html(`<i class="fa-brands fa-whatsapp fa-fw me-2"></i>${getMessage('project.share-on-whatsapp')}`)
        .on('click', function () {shareOnWhatsApp($(this))});
    shareOnWhatsAppItem.append(shareOnWhatsAppBtn);
    shareDropDownMenu.append(shareOnWhatsAppItem);
    likesCommentsShareCol.append(shareBtn);
    likesCommentsShareCol.append(shareDropDownMenu);

    let viewsCol = $('<div></div>').addClass('col-4 text-end');
    let viewsSymbol = $('<i></i>').addClass('fa-regular fa-eye').css('color', '#a1a0a0');
    let viewsCounter = $('<span></span>').addClass('ms-1 text-secondary-emphasis small').text(project.views);
    viewsCol.append(viewsSymbol);
    viewsCol.append(viewsCounter);
    likesCommentsShareViewsRow.append(viewsCol);
    cardBody.append(likesCommentsShareViewsRow);

    let projectLink = $('<a></a>').addClass('stretched-link').attr('href', `/projects/${project.id}/view`);
    cardBody.append(projectLink);

    card.append(cardBody);

    let footer = $('<div></div>').addClass('card-footer').css('position', 'relative').css('z-index', '1').css('min-height', '78px');
    let technologiesDiv = $('<div></div>');
    footer.append(technologiesDiv);
    project.technologies.forEach(technology => {
        let technologyLink = $('<a></a>').attr('type', 'button').attr('target', '_blank')
            .attr('title', technology.name).attr('href', technology.url).addClass('me-1 mb-1')
            .css('text-decoration', 'none');
        let technologyLogo = $('<img>').attr('src', `/${technology.logo.fileLink}`).attr('width', 24)
            .attr('height', 24)
            .on('mouseenter', function () {
                $(this).addClass('opacity-75');
            })
            .on('mouseleave', function () {
                $(this).removeClass('opacity-75');
            });
        technologyLink.append(technologyLogo);
        technologiesDiv.append(technologyLink);
    });

    card.append(footer);
    return card;
}

function likeProject(likeBtn, id) {
    if (authUser != null) {
        let likeIcon = $(likeBtn).find('i');
        let likeCounter = $(likeBtn).find('span');
        let liked = likeIcon.attr('class').includes('fa-regular');
        $.ajax({
            url: `/projects/${id}/like`,
            type: "PATCH",
            data: {id: id, liked: liked},
        }).done(function() {
            likeIcon.removeClass(liked ? 'fa-regular' : 'fa-solid').addClass(liked ? 'fa-solid' : 'fa-regular');
            likeCounter.text(+(likeCounter.text()) + (liked ? 1 : -1));
        }).fail(function(data) {
            likeIcon.removeClass(liked ? 'fa-solid' : 'fa-regular').addClass(liked ? 'fa-regular' : 'fa-solid');
            handleError(data,  getMessage(liked ? 'project.failed-to-like' : 'project.failed-to-dislike'));
        });
    } else  {
        $('.with-popover').popover('hide');
        $(likeBtn).popover('toggle');
    }
}


function formatDateTime(dateTime) {
    let dateAndTime = dateTime.split('T');
    let dateParts =  dateAndTime[0].split('-');
    let formattedDate = `${dateParts[2]}.${dateParts[1]}.${dateParts[0]}`;
    let formattedTime = dateAndTime[1].substring(0, dateAndTime[1].lastIndexOf(':'));
    return `${formattedDate} ${formattedTime}`;
}

function loadMoreTags() {
    loadMoreTagsBtn.attr('hidden', true);
    tagsCurrentPage++;
    let keyword = new URLSearchParams(window.location.search).get('keyword');

    $.ajax({
        url: '/search/tags',
        data: { keyword: keyword, page: tagsCurrentPage, size: 9 }
    }).done(tagsPage => {
        if (tagsPage.content.length !== 0) {
            tagsPage.content.forEach(tag => {
                tagsArea.append(generateTagDiv(tag));
            });
            if (tagsPage.total !== $('.tag-div').length) {
                loadMoreTagsBtn.attr('hidden', false);
            }
        }
    }).fail(function(data) {
        handleError(data, getMessage('info.failed-to-get-tags'));
        tagsCurrentPage--;
        loadMoreTagsBtn.attr('hidden', false);
    });
}

function generateTagDiv(tag) {
    let tagDiv = $('<div></div>').addClass('tag-div mb-3');
    let tagLink = $('<a></a>').addClass('text-decoration-none').attr('href', `/tags/${tag.name}`)
        .html(`#${tag.name}`);
    tagDiv.append(tagLink);
    return tagDiv;
}