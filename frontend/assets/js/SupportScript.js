$(document).ready(function() {
    // Dummy data to simulate backend response
    const tickets = [
        { ticketId: 'T001', userId: 'U101', issueDescription: 'My claim for policy P123 was denied, but I believe it is valid.', ticketStatus: 'OPEN', createdDate: '2023-10-26' },
        { ticketId: 'T002', userId: 'U101', issueDescription: 'I need to update my personal information on my policy.', ticketStatus: 'RESOLVED', createdDate: '2023-10-20' },
        { ticketId: 'T003', userId: 'U101', issueDescription: 'The portal is not loading my claim history.', ticketStatus: 'OPEN', createdDate: '2023-10-25' }
    ];

    // Function to render tickets on the dashboard
    function renderTickets() {
        const tableBody = $('#tickets-table-body');
        tableBody.empty(); // Clear existing rows
        
        tickets.forEach(ticket => {
            const statusClass = ticket.ticketStatus === 'OPEN' ? 'text-danger' : 'text-success';
            const row = `
                <tr>
                    <td>${ticket.ticketId}</td>
                    <td>${ticket.issueDescription}</td>
                    <td class="${statusClass}">${ticket.ticketStatus}</td>
                    <td>${ticket.createdDate}</td>
                    <td><button class="btn btn-info btn-sm view-details-btn" data-ticket-id="${ticket.ticketId}">View Details</button></td>
                </tr>
            `;
            tableBody.append(row);
        });
    }

    // Initial render
    renderTickets();

    // Show create ticket form
    $('#createTicketBtn').on('click', function() {
        $('#user-dashboard').hide();
        $('#create-ticket-form-section').show();
    });

    // Handle form submission (create new ticket)
    $('#ticket-form').on('submit', function(event) {
        event.preventDefault(); // Prevent default form submission

        const issueDescription = $('#issueDescription').val();
        
        // In a real application, you would send this data to your backend
        // For this example, we'll just add it to our dummy data
        const newTicket = {
            ticketId: 'T' + String(tickets.length + 1).padStart(3, '0'),
            userId: 'U101',
            issueDescription: issueDescription,
            ticketStatus: 'OPEN',
            createdDate: new Date().toISOString().slice(0, 10)
        };
        tickets.push(newTicket);

        alert('Ticket submitted successfully!');

        // Clear form and go back to dashboard
        $('#issueDescription').val('');
        $('#create-ticket-form-section').hide();
        $('#user-dashboard').show();
        renderTickets(); // Re-render the table with the new ticket
    });

    // Handle button to go back to dashboard
    $('#backToDashboardBtn').on('click', function() {
        $('#create-ticket-form-section').hide();
        $('#user-dashboard').show();
    });

    // Handle view details button click
    $(document).on('click', '.view-details-btn', function() {
        const ticketId = $(this).data('ticket-id');
        const ticket = tickets.find(t => t.ticketId === ticketId);

        if (ticket) {
            const modalBody = $('#modal-body-content');
            modalBody.empty();
            const detailsHtml = `
                <p><strong>Ticket ID:</strong> ${ticket.ticketId}</p>
                <p><strong>Status:</strong> <span class="${ticket.ticketStatus === 'OPEN' ? 'text-danger' : 'text-success'}">${ticket.ticketStatus}</span></p>
                <p><strong>Created:</strong> ${ticket.createdDate}</p>
                <p><strong>Description:</strong></p>
                <p>${ticket.issueDescription}</p>
            `;
            modalBody.append(detailsHtml);
            $('#ticketDetailsModal').modal('show');
        }
    });
});