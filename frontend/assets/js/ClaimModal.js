// Load form into modal container
$(function () {
  $("#claimFormModalContainer").load("/frontend/pages/ClaimForm.html");
});

// Handle form submission (delegated binding)
$(document).on("submit", "#claimFormModal", function (e) {
  e.preventDefault();

  const newClaim = {
    id: "CLM-" + Math.floor(Math.random() * 9000 + 1000),
    policy: $("#policyId").val(),
    type: $("#claimType").val(),
    amount: $("#claimAmount").val(),
    status: "Submitted",
    date: new Date().toISOString().split("T")[0]
  };

  $("#claimsTable tbody").append(`
    <tr>
      <td>${newClaim.id}</td>
      <td>${newClaim.policy}</td>
      <td>${newClaim.type}</td>
      <td>₹${newClaim.amount}</td>
      <td>${newClaim.status}</td>
      <td>${newClaim.date}</td>
      <td><button class="btn btn-sm btn-info">View</button></td>
    </tr>
  `);

  $("#claimFormModal").modal("hide");
  $("#claimForm")[0].reset();
});
