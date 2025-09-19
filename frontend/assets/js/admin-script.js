$(document).ready(function() {
    // Dummy data for all tickets (simulates backend data)
    let allTickets = [
        { ticketId: 'T001', userId: 'U101', issueDescription: 'My claim for policy P123 was denied, but I believe it is valid.', ticketStatus: 'OPEN', createdDate: '2023-10-26' },
        { ticketId: 'T002', userId: 'U101', issueDescription: 'I need to update my personal information on my policy.', ticketStatus: 'RESOLVED', createdDate: '2023-10-20' },
        { ticketId: 'T003', userId: 'U101', issueDescription: 'The portal is not loading my claim history.', ticketStatus: 'OPEN', createdDate: '2023-10-25' },
        { ticketId: 'T004', userId: 'U102', issueDescription: 'Claim status for C456 is stuck on "Pending".', ticketStatus: 'OPEN', createdDate: '2023-10-24' }
    ];

    // Function to render the table and update counts
    function renderAdminTickets() {
        const tableBody = $('#admin-tickets-table-body');
        tableBody.empty(); // Clear existing rows

        let openCount = 0;
        let resolvedCount = 0;
        
        allTickets.forEach(ticket => {
            const statusClass = ticket.ticketStatus === 'OPEN' ? 'badge bg-warning' : 'badge bg-success';
            const row = `
                <tr>
                    <td>${ticket.ticketId}</td>
                    <td>${ticket.userId}</td>
                    <td>${ticket.issueDescription.substring(0, 50)}...</td>
                    <td><span class="${statusClass}">${ticket.ticketStatus}</span></td>
                    <td>${ticket.createdDate}</td>
                    <td><button class="btn btn-info btn-sm view-admin-ticket-btn" data-ticket-id="${ticket.ticketId}">View & Reply</button></td>
                </tr>
            `;
            tableBody.append(row);

            if (ticket.ticketStatus === 'OPEN') {
                openCount++;
            } else {
                resolvedCount++;
            }
        });

        $('#open-tickets-count').text(openCount);
        $('#resolved-tickets-count').text(resolvedCount);
    }

    // Initial render
    renderAdminTickets();

    // Handle view & reply button click
    $(document).on('click', '.view-admin-ticket-btn', function() {
        const ticketId = $(this).data('ticket-id');
        const ticket = allTickets.find(t => t.ticketId === ticketId);

        if (ticket) {
            // Populate modal with ticket details
            $('#modalTicketId').text(ticket.ticketId);
            $('#modalUserId').text(ticket.userId);
            $('#modalIssueDescription').text(ticket.issueDescription);

            const statusClass = ticket.ticketStatus === 'OPEN' ? 'badge bg-warning' : 'badge bg-success';
            $('#modalTicketStatus').html(`<span class="${statusClass}">${ticket.ticketStatus}</span>`);
            
            // Show/hide resolve button based on status
            if (ticket.ticketStatus === 'RESOLVED') {
                $('#resolveTicketBtn').hide();
                $('#adminReply').prop('disabled', true).attr('placeholder', 'Ticket is resolved.');
            } else {
                $('#resolveTicketBtn').show();
                $('#adminReply').prop('disabled', false).attr('placeholder', 'Type your reply here...');
            }

            // Show the modal
            $('#adminTicketModal').modal('show');
        }
    });

    // Handle reply form submission
    $('#reply-form').on('submit', function(event) {
        event.preventDefault();
        const replyText = $('#adminReply').val();
        const currentTicketId = $('#modalTicketId').text();

        if (replyText.trim() !== '') {
            // In a real app, send the reply to the backend for the specified ticketId
            alert(`Reply sent for ticket ${currentTicketId}: "${replyText}"`);
            $('#adminReply').val(''); // Clear the textarea
        } else {
            alert('Reply cannot be empty.');
        }
    });

    // Handle "Resolve Ticket" button click
    $('#resolveTicketBtn').on('click', function() {
        const currentTicketId = $('#modalTicketId').text();
        const ticketIndex = allTickets.findIndex(t => t.ticketId === currentTicketId);

        if (ticketIndex !== -1) {
            // In a real app, send a request to the backend to change the status
            allTickets[ticketIndex].ticketStatus = 'RESOLVED';
            alert(`Ticket ${currentTicketId} has been resolved.`);
            $('#adminTicketModal').modal('hide');
            renderAdminTickets(); // Re-render the table with updated status
        }
    });
});