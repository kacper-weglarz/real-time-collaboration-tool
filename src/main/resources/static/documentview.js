let documentData = null;
let stompClient = null;


const titleInput = document.querySelector('.title');
const contentInput = document.querySelector('.content');
let timeoutId;
const debounceDelay = 300; //0.3 sek

/**
 * Loads the chosen document by ID
 */
function loadChosenDocument(id) {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'userprofile.html';
    } else {
        fetch(`/api/document/${id}`, {
            method: 'GET',
            headers: { 'Authorization': 'Bearer ' + token }
        })
            .then(response => response.json())
            .then(doc => {
                documentData = doc; // -> store doc object from response for role usage
                const title = document.querySelector('.title');
                const content = document.querySelector('.content');
                title.textContent = doc.title;
                content.textContent = doc.content;
                console.log(doc);
            })
            .catch(err => console.error('Fetch error:', err));
    }
}

/**
 * Updates the document
 */
function updateDocument() {
    const token = localStorage.getItem('token');
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id');

    if (!token || !id) return;

    fetch(`/api/document/${id}`, {
        method: 'PUT',
        headers: {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            title: titleInput.textContent,
            content: contentInput.textContent,
        })
    })
        .then(res => {
            if (!res.ok) throw new Error('Save failed');
            return res.json();
        })
        .then(doc => {
            console.log(doc);
        })
        .catch(err => console.error('Auto-save error:', err));
}

/**
 * Deletes the document
 */
function deleteDocument() {
    const token = localStorage.getItem('token');
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id');

    if (!token || !id) {
        window.location.href = 'userprofile.html';
    } else {
        fetch(`/api/document/${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        })
            .then(res => {
                if (res.ok) {
                    window.location.href = 'userprofile.html';
                } else {
                    throw new Error('Failed to delete document');
                }
            })
            .catch(error => {
                console.error('Error deleting document:', error);
            });
    }
}

/**
 * Creates a new document
 */
function createNewDocument() {
    const token = localStorage.getItem('token');

    if (!token) {
        window.location.href = '/userprofile.html';
    } else {
        fetch('/api/document', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                title: "Document without title",
                content: "",
            })
        })
            .then(r => {
                if (!r.ok)
                    throw new Error("Failed to create a document");
                return r.json();
            })
            .then(newDoc => {
                window.location.href = `/documentview.html?id=${newDoc.id}`;
            })
            .then(err => {
                console.error("Error: " + err);
            })
    }
}

/**
 * Connects to WebSocket
 */
function connectWS(docId) {
    const socket = new SockJS('/ws'); // SockJS connection
    stompClient = Stomp.over(socket); //SockJs w STOMP

    stompClient.connect({}, () => {
        console.log('polaczenie: z dokumentem ', docId);

        stompClient.subscribe(`/topic/document/${docId}`, (message) => {
            const edit = JSON.parse(message.body); // -> parse edit JSON
            applyEdit(edit);
        });
    }, (error) => {
        console.error('error: ', error);
    });
}

/**
 * Sends edits to users
 */
function sendEdit(docId) {
    if (stompClient && stompClient.connected) {
        const edit = {
            title: titleInput.textContent,
            content: contentInput.textContent
        };

        stompClient.send(`/app/document/${docId}/edit`, {}, JSON.stringify(edit));
        console.log('wyslano zmiane');
    }
}

/**
 * Applies received edits but not immediately if user is typing
 */
let lastReceivedEdit = null;

function applyEdit(edit) {
    lastReceivedEdit = edit;
    const activeElement = document.activeElement;

    if (edit.title !== undefined) {
        if (activeElement !== titleInput) {
            titleInput.textContent = edit.title;
        }
    }
    if (edit.content !== undefined) {
        if (activeElement !== contentInput) {
            contentInput.textContent = edit.content;
        }
    }
}

/**
 * Logout
 */
function logout() {
    localStorage.removeItem('token');
    window.location.href = '/loginsignup.html';
}

/**
 * Share document
 */
function shareDocument() {
    const token = localStorage.getItem('token');
    const params = new URLSearchParams(window.location.search);
    const docid = params.get('id');
    const username = document.getElementById('username').value;
    const role = document.getElementById('role').value;

    if (!token) {
        throw new Error('Unauthorized');
    } else {
        fetch(`/api/document/${docid}/share`, {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({role , username})
        })
            .then(r => {
                if (!r.ok)
                    throw new Error("Failed to create a document");
                return r.json();
            })
            .then(data => {
                alert('Document shared successfully!');
                document.getElementById('shareDialog').close();
                document.getElementById('shareForm').reset();
            })
            .catch(err => {
                console.error("Error:", err);
                alert('Failed to share document');
            });
    }
    document.getElementById('shareDialog').close();
}

/**
 * On HTML load, gets URL parameters and loads document by ID
 */
document.addEventListener('DOMContentLoaded', () => {
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id');

    if (id) {
        loadChosenDocument(id)
        connectWS(id);
    } else {
        window.location.href = "/userprofile.html"
    }
})

// -> trigger autosave
titleInput.addEventListener('input', scheduleAutoSave);
contentInput.addEventListener('input', scheduleAutoSave);


document.getElementById('delete-doc').addEventListener('click', (e) => {
    e.preventDefault();
    deleteDocument();
})


document.getElementById('create-doc').addEventListener('click', (e) => {
    e.preventDefault();
    createNewDocument();
})

document.getElementById('share-doc').addEventListener('click', (e) => {
    e.preventDefault();

    const roleSelect = document.getElementById('role');

    if (documentData && documentData.currentUserRole !== 'OWNER') {
        roleSelect.querySelector('option[value="EDITOR"]').remove();
    }

    document.getElementById('shareDialog').showModal();
})

document.getElementById('shareForm').addEventListener('submit', (e) => {
    e.preventDefault();
    shareDocument();
})

document.getElementById('closeDialog').addEventListener('click', (e) => {
    e.preventDefault();
    document.getElementById('shareDialog').close();
})

document.getElementById('go-back').addEventListener('click', (e) => {
    e.preventDefault();
    window.location.href = 'userprofile.html';
})

document.getElementById("logout").addEventListener('click', (e) => {
    e.preventDefault();
    logout();
})

titleInput.addEventListener('blur', () => {
    if (lastReceivedEdit && lastReceivedEdit.title !== undefined) {
        titleInput.textContent = lastReceivedEdit.title;
    }
});

contentInput.addEventListener('blur', () => {
    if (lastReceivedEdit && lastReceivedEdit.content !== undefined) {
        contentInput.textContent = lastReceivedEdit.content;
    }
});

/**
 * Autosave function with delay, saves 0.3s after last change
 */
function scheduleAutoSave() {
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id');
    sendEdit(id);

    clearTimeout(timeoutId);  //-> clear previous timer if user keeps typing
    timeoutId = setTimeout(() => { //-> set new timer
        updateDocument(); //-> save document
    }, debounceDelay);
}
