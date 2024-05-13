
const baseUrl = "http://localhost:8080/books/";

document.addEventListener('DOMContentLoaded', getAllBooks);

function ajaxRequest(method, url, body, callback) {
    var xhr = new XMLHttpRequest();
    xhr.open(method, url, true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            callback(xhr.status, xhr.responseText);
        }
    };
    if (method === 'DELETE') {
        xhr.send(null);
    } else {
        xhr.send(JSON.stringify(body));
    }
}

function getAllBooks() {
    ajaxRequest("GET", baseUrl, null, function(status, response) {
        if (status === 200) {
            const data = JSON.parse(response);
            const tableBody = document.getElementById('itemsTable').getElementsByTagName('tbody')[0];
            tableBody.innerHTML = '';


            if (data._embedded && data._embedded.bookList) {
                const books = data._embedded.bookList;
                books.forEach((book) => {
                    const row = tableBody.insertRow();
                    row.insertCell(0).innerText = book.id;
                    row.insertCell(1).innerText = book.title;
                    row.insertCell(2).innerText = book.author;
                    //row.insertCell(3).innerText = book.year;
                    row.insertCell(3).innerText = book.status;
                    const actionsCell = row.insertCell(4);
                    createActionButton(book, actionsCell);
                });
            } else {

                const row = tableBody.insertRow();
                const cell = row.insertCell(0);
                cell.innerText = "No books available";
                cell.colSpan = 6;
            }
        } else {
            console.error("Error fetching books:", status, response);
        }
    });
}


function createActionButton(book, cell) {
    const editButton = document.createElement('button');
    editButton.innerText = 'Edit';
    editButton.onclick = () => openEditBook(book);
    cell.appendChild(editButton);
    if (book._links.borrow) {
        const borrowButton = document.createElement("button");
        borrowButton.innerText = "Borrow";
        borrowButton.onclick = () => changeBookStatus(book._links.borrow.href);
        cell.appendChild(borrowButton);
    }
    if (book._links.return) {
        const returnButton = document.createElement("button");
        returnButton.innerText = "Return";
        returnButton.onclick = () => changeBookStatus(book._links.return.href);
        cell.appendChild(returnButton);
    }
    if (book._links.activate) {
        const activateButton = document.createElement("button");
        activateButton.innerText = "Activate";
        activateButton.onclick = () => changeBookStatus(book._links.activate.href);
        cell.appendChild(activateButton);
    }
    if (book._links.deactivate) {
        const deactivateButton = document.createElement("button");
        deactivateButton.innerText = "Deactivate";
        deactivateButton.onclick = () => changeBookStatus(book._links.deactivate.href);
        cell.appendChild(deactivateButton);
    }
    if (book._links.delete) {
        const deleteButton = document.createElement("button");
        deleteButton.innerText = "Delete";
        //deleteButton.onclick = () => changeBookStatus(book._links.delete.href, 'DELETE'); // Assuming delete might use a different method
        deleteButton.onclick = () => deleteBook(book._links.delete.href);

        cell.appendChild(deleteButton);
    }
}


function changeBookStatus(url) {
    ajaxRequest("PATCH", url, null, function(status, response) {
        if (status === 200) {
            //alert('Operation successful.');
            getAllBooks(); // Refresh the list
        } else {
            alert('Operation failed.');
        }
    });
}

function createBook() {
    const book = {
        id: document.getElementById('bookId').value,
        title: document.getElementById('bookTitle').value,
        author: document.getElementById('bookAuthor').value,
        year: parseInt(document.getElementById('bookYear').value, 10)
    };
    ajaxRequest("POST", baseUrl, book, function(status, response) {
        if (status === 201) {
            //alert("Book added successfully.");
            getAllBooks();
        } else {
            alert("Failed to add the book.");
        }
    });
}

function deleteBook(url) {
    ajaxRequest('DELETE', url, null, function(status, response) {
        if (status === 204 || status === 200) {  // 204 No Content or 200 OK
            //alert('Book deleted successfully.');
            getAllBooks();
        } else {
            alert('Failed to delete book. Status: ' + status);
        }
    });
}

function saveBook() {
    const bookId = document.getElementById('editBookId').value;
    const updatedBook = {
        title: document.getElementById('editBookTitle').value,
        author: document.getElementById('editBookAuthor').value,
        year: parseInt(document.getElementById('editBookYear').value, 10)
    };
    ajaxRequest("PUT", `${baseUrl}${bookId}`, updatedBook, function(status, response) {
        if (status === 200) {
            //alert("Book updated successfully.");
            document.getElementById('editBookModal').style.display = 'none';
            getAllBooks();
        } else {
            alert("Failed to update the book.");
        }
    });
}

function openEditBook(book) {
    document.getElementById('editBookModal').style.display = 'block';
    document.getElementById('editBookId').value = book.id;
    document.getElementById('editBookTitle').value = book.title;
    document.getElementById('editBookAuthor').value = book.author;
    document.getElementById('editBookYear').value = book.year;
}

function closeEditModal() {
    document.getElementById('editBookModal').style.display = 'none';
}



