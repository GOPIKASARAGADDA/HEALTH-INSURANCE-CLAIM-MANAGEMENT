/* claims.js
   Full front-end claim management behavior (demo mode).
   Replace the mock functions with real AJAX calls to Spring Boot when backend is ready.
*/
 
/* ------------------------------
   In-memory sample claims (demo)
   ------------------------------ */
let claims = [
  {
    claimId: "CLM-1001",
    policyId: "POL-9001",
    userId: "U-101",
    claimant: "John Doe",
    type: "Hospitalization",
    amount: 8500,
    status: "Pending",
    submitted: "2025-09-08",
    provider: "City General Hospital",
    remarks: "",
    assignedTo: null
  },
  {
    claimId: "CLM-1002",
    policyId: "POL-9002",
    userId: "U-102",
    claimant: "Priya K",
    type: "Accident",
    amount: 12500,
    status: "Verified",
    submitted: "2025-09-07",
    provider: "Trauma Care Center",
    remarks: "verified by adjuster",
    assignedTo: "adjuster-1"
  },
  {
    claimId: "CLM-1003",
    policyId: "POL-9001",
    userId: "U-101",
    claimant: "John Doe",
    type: "OPD",
    amount: 1200,
    status: "Approved",
    submitted: "2025-08-30",
    provider: "WellCare Clinic",
    remarks: "approved by admin",
    assignedTo: "adjuster-2"
  }
];
 
let currentRole = (currentUser && currentUser.role) || "Guest";
 
/* ------------------------------
   Helper functions
   ------------------------------ */
function badgeForStatus(status) {
  switch (status) {
    case "Pending": return '<span class="badge-status badge-pending">Pending</span>';
    case "Verified": return '<span class="badge-status badge-verified">Verified</span>';
    case "Approved": return '<span class="badge-status badge-approved">Approved</span>';
    case "Rejected": return '<span class="badge-status badge-rejected">Rejected</span>';
    case "NeedInfo": return '<span class="badge-status badge-needs">Need More Info</span>';
    default: return `<span class="badge bg-secondary">${status}</span>`;
  }
}
 
/* ------------------------------
   Rendering UI
   ------------------------------ */
function renderActionButtons() {
  const $box = $("#actionButtons");
  $box.empty();
 
  if (!isLoggedIn) {
    $box.append(`<button class="btn btn-outline-primary" disabled>Login to manage claims</button>`);
    return;
  }
 
  const role = currentUser.role;
  $("#topUserBadge").text(currentUser.name + (currentUser.role ? ` (${currentUser.role})` : ""));
 
  if (role === "Policyholder") {
    $box.append(`<button id="openFileModal" class="btn btn-primary">File New Claim</button>`);
    $box.append(`<button id="myClaimsBtn" class="btn btn-outline-secondary">My Claims</button>`);
  } else if (role === "Agent") {
    $box.append(`<button id="openFileModal" class="btn btn-primary">File Claim for Customer</button>`);
    $box.append(`<button id="agentView" class="btn btn-outline-secondary">My Customers' Claims</button>`);
  } else if (role === "Claim Adjuster") {
    $box.append(`<button id="assignedClaims" class="btn btn-primary">Assigned Claims</button>`);
    $box.append(`<button id="adjusterRefresh" class="btn btn-outline-secondary">Refresh</button>`);
  } else if (role === "Admin") {
    $box.append(`<button id="pendingQueue" class="btn btn-primary">Pending Approvals</button>`);
    $box.append(`<button id="reportsBtn" class="btn btn-outline-secondary">Reports</button>`);
  }
}
 
function renderTable() {
  const $tbody = $("#claimsTable tbody");
  $tbody.empty();
 
  if (!isLoggedIn) {
    $tbody.append(`<tr><td colspan="7" class="text-center text-muted">Login to view claims.</td></tr>`);
    return;
  }
 
  const q = $("#searchBox").val().toLowerCase();
  const statusFilter = $("#filterStatus").val();
  const typeFilter = $("#filterType").val();
  const role = currentUser.role;
 
  // role-based visibility simulation
  const visible = claims.filter(c => {
    if (role === "Policyholder") {
      // simulate userId U-101 for demo
      if (c.userId !== "U-101") return false;
    } else if (role === "Agent") {
      // agent sees certain policies - demo: POL-9001
      if (!["POL-9001"].includes(c.policyId)) return false;
    } else if (role === "Claim Adjuster") {
      if (!c.assignedTo || c.assignedTo !== "adjuster-1") return false;
    } // Admin sees all
 
    if (statusFilter && c.status !== statusFilter) return false;
    if (typeFilter && c.type !== typeFilter) return false;
 
    if (q) {
      const hay = `${c.claimId} ${c.policyId} ${c.provider} ${c.type} ${c.claimant} ${c.status}`.toLowerCase();
      if (!hay.includes(q)) return false;
    }
    return true;
  });
 
  if (!visible.length) {
    $tbody.append(`<tr><td colspan="7" class="text-center text-muted">No claims found for this view.</td></tr>`);
    return;
  }
 
  visible.forEach(c => {
    $tbody.append(`
      <tr>
        <td>${c.claimId}</td>
        <td>${c.policyId}</td>
        <td>${c.type}</td>
        <td>₹ ${Number(c.amount).toLocaleString(undefined, {minimumFractionDigits:2, maximumFractionDigits:2})}</td>
        <td>${badgeForStatus(c.status)}</td>
        <td>${c.submitted}</td>
        <td class="text-end">${renderActionsForRow(c)}</td>
      </tr>
    `);
  });
}
 
function renderActionsForRow(c) {
  const role = currentUser.role;
  const viewBtn = `<button class="btn btn-sm btn-outline-primary view-claim" data-id="${c.claimId}"><i class="bi bi-eye"></i> View</button>`;
 
  if (role === "Policyholder") {
    return viewBtn + ` <button class="btn btn-sm btn-outline-secondary ms-1 download-claim" data-id="${c.claimId}"><i class="bi bi-download"></i></button>`;
  } else if (role === "Agent") {
    return viewBtn + ` <button class="btn btn-sm btn-outline-success ms-1 assist-claim" data-id="${c.claimId}"><i class="bi bi-person-lines-fill"></i></button>`;
  } else if (role === "Claim Adjuster") {
    return viewBtn + ` <div class="btn-group ms-1"><button class="btn btn-sm btn-success verify-claim" data-id="${c.claimId}">Verify</button><button class="btn btn-sm btn-warning needinfo-claim" data-id="${c.claimId}">Request Info</button></div>`;
  } else if (role === "Admin") {
    return viewBtn + ` <div class="btn-group ms-1"><button class="btn btn-sm btn-success approve-claim" data-id="${c.claimId}">Approve</button><button class="btn btn-sm btn-danger reject-claim" data-id="${c.claimId}">Reject</button><button class="btn btn-sm btn-secondary assign-claim" data-id="${c.claimId}">Assign</button></div>`;
  } else {
    return viewBtn;
  }
}
 
/* ------------------------------
   Event handlers
   ------------------------------ */
function setupHandlers() {
  // role changes from navbar
  $(document).on("roleChanged authChanged", function (e, data) {
    if (isLoggedIn && currentUser) {
      renderActionButtons();
      renderTable();
    } else {
      renderActionButtons();
      renderTable();
      $("#topUserBadge").text("Guest");
    }
  });
 
  // Search & filters
  $("#searchBox").on("input", renderTable);
  $("#filterStatus, #filterType").on("change", renderTable);
  $("#refreshBtn").on("click", renderTable);
 
  // Action buttons (delegation)
  $("#actionButtons").on("click", "#openFileModal", function () { $("#fileClaimModal").modal("show"); });
  $("#actionButtons").on("click", "#myClaimsBtn", function () { $("#filterStatus").val(""); $("#filterType").val(""); renderTable(); });
 
  // Table action buttons
  $("#claimsTable tbody").on("click", ".view-claim", function () {
    const id = $(this).data("id"); openClaimDetail(id);
  });
  $("#claimsTable tbody").on("click", ".approve-claim", function () {
    const id = $(this).data("id"); updateClaimStatus(id, "Approved", "Approved by admin");
  });
  $("#claimsTable tbody").on("click", ".reject-claim", function () {
    const id = $(this).data("id"); updateClaimStatus(id, "Rejected", "Rejected by admin");
  });
  $("#claimsTable tbody").on("click", ".assign-claim", function () {
    const id = $(this).data("id"); assignClaimTo(id, "adjuster-1");
  });
  $("#claimsTable tbody").on("click", ".verify-claim", function () {
    const id = $(this).data("id"); updateClaimStatus(id, "Verified", "Verified by adjuster");
  });
  $("#claimsTable tbody").on("click", ".needinfo-claim", function () {
    const id = $(this).data("id"); updateClaimStatus(id, "NeedInfo", "Adjuster requested more documents");
  });
  $("#claimsTable tbody").on("click", ".assist-claim", function () {
    const id = $(this).data("id");
    const c = claims.find(x => x.claimId === id);
    $("#fileClaimModal").modal("show");
    $("#policyId").val(c.policyId);
    $("#claimantName").val(c.claimant);
  });
 
  // File claim modal submit
  $("#fileClaimForm").on("submit", function (e) {
    e.preventDefault();
    const form = {
      claimant: $("#claimantName").val(),
      policyId: $("#policyId").val(),
      claimType: $("#claimType").val(),
      incidentDate: $("#incidentDate").val(),
      provider: $("#provider").val(),
      claimAmount: parseFloat($("#claimAmount").val()),
      remarks: $("#remarks").val()
      // documents omitted (demo)
    };
    submitClaim(form);
    $("#fileClaimModal").modal("hide");
    this.reset();
  });
}
 
/* ------------------------------
   Modal & Detail views
   ------------------------------ */
function openClaimDetail(claimId) {
  const c = claims.find(x => x.claimId === claimId);
  if (!c) return alert("Claim not found");
 
  $("#detailModalTitle").text(`${c.claimId} — ${c.type}`);
  const html = `
    <div class="row g-2 mb-2">
      <div class="col-md-6"><strong>Policy ID:</strong> ${c.policyId}</div>
      <div class="col-md-6"><strong>Submitted:</strong> ${c.submitted}</div>
      <div class="col-md-6"><strong>Claimant:</strong> ${c.claimant}</div>
      <div class="col-md-6"><strong>Amount:</strong> ₹ ${c.amount.toFixed(2)}</div>
    </div>
    <div class="row g-2 mb-2">
      <div class="col-12"><strong>Provider:</strong> ${c.provider || "-"}</div>
    </div>
    <div class="row g-2 mb-2">
      <div class="col-12"><strong>Remarks:</strong><br> ${c.remarks || '<span class="text-muted">-</span>'}</div>
    </div>
    <hr/>
    <div class="small text-muted">Assigned To: ${c.assignedTo || 'Not assigned'}</div>
  `;
  $("#detailBody").html(html);
 
  const $f = $("#detailFooter");
  $f.empty();
  $f.append(`<button class="btn btn-sm btn-secondary" data-bs-dismiss="modal">Close</button>`);
 
  if (isLoggedIn && currentUser.role === "Claim Adjuster" && (c.assignedTo === null || c.assignedTo === "adjuster-1")) {
    $f.append(`<button class="btn btn-sm btn-success ms-2" id="detailVerify">Verify</button>`);
    $f.append(`<button class="btn btn-sm btn-warning ms-2" id="detailNeedInfo">Request Info</button>`);
    $("#detailVerify").on("click", function () { updateClaimStatus(claimId, "Verified", "Verified by adjuster"); $("#claimDetailModal").modal("hide"); });
    $("#detailNeedInfo").on("click", function () { updateClaimStatus(claimId, "NeedInfo", "Need more docs"); $("#claimDetailModal").modal("hide"); });
  }
 
  if (isLoggedIn && currentUser.role === "Admin") {
    $f.append(`<div class="btn-group ms-2">
                <button class="btn btn-success btn-sm" id="detailApprove">Approve</button>
                <button class="btn btn-danger btn-sm" id="detailReject">Reject</button>
                <button class="btn btn-secondary btn-sm" id="detailAssign">Assign</button>
                </div>`);

                    $("#detailApprove").on("click", function () { updateClaimStatus(claimId, "Approved", "Approved by admin"); $("#claimDetailModal").modal("hide"); });
    $("#detailReject").on("click", function () { updateClaimStatus(claimId, "Rejected", "Rejected by admin"); $("#claimDetailModal").modal("hide"); });
    $("#detailAssign").on("click", function () { assignClaimTo(claimId, "adjuster-1"); $("#claimDetailModal").modal("hide"); });
  }
 
  $("#claimDetailModal").modal("show");
}
 
/* ------------------------------
   Mock backend controller wrappers
   Replace AJAX inside these when real backend exists.
   ------------------------------ */
 
function submitClaim(form) {
  // Demo: create unique ID
  const id = "CLM-" + Math.floor(1000 + Math.random()*9000);
  const newClaim = {
    claimId: id,
    policyId: form.policyId,
    userId: (currentUser && currentUser.role === "Policyholder") ? "U-101" : "U-XXX",
    claimant: form.claimant,
    type: form.claimType,
    amount: (form.claimAmount || 0),
    status: "Pending",
    submitted: new Date().toISOString().slice(0,10),
    provider: form.provider || "",
    remarks: form.remarks || "",
    assignedTo: null
  };
  // In real app: POST /api/claims (submitClaim)
  claims.unshift(newClaim);
  renderTable();
  alert("Claim submitted (demo): " + id);
}
 
function getClaimDetails(claimId) {
  // In real app: GET /api/claims/{id}
  return claims.find(c => c.claimId === claimId) || null;
}
 
function updateClaimStatus(claimId, status, remark) {
  // In real app: PUT /api/claims/{id}/status
  const c = claims.find(x => x.claimId === claimId);
  if (!c) return alert("Claim not found");
  c.status = status;
  c.remarks = (c.remarks ? c.remarks + " | " : "") + (remark || status);
  renderTable();
}
 
function getAllClaimsByPolicy(policyId) {
  // In real app: GET /api/claims?policyId=XXX
  return claims.filter(c => c.policyId === policyId);
}
 
function assignClaimTo(claimId, adjusterId) {
  const c = claims.find(x => x.claimId === claimId);
  if (!c) return alert("Claim not found");
  c.assignedTo = adjusterId;
  c.remarks = (c.remarks ? c.remarks + " | " : "") + `Assigned to ${adjusterId}`;
  renderTable();
  alert(`Assigned ${claimId} to ${adjusterId} (demo)`);
}
 
/* ------------------------------
   Init
   ------------------------------ */
$(function () {
  // Setup UI handlers
  setupHandlers();
 
  // If navbar already had a logged in user, use that; else guest
  if (typeof currentUser !== "undefined" && currentUser.role) {
    // currentUser variable comes from navbar.js global; if not present, default to guest
  } else {
    currentUser = { name: "Guest", role: "Guest" };
    isLoggedIn = false;
  }
 
  // react to role changes from navbar
  $(document).on("roleChanged", function (e, user) {
    if (user && user.role) {
      currentUser = user;
      isLoggedIn = true;
    } else {
      currentUser = { name: "Guest", role: "Guest" };
      isLoggedIn = false;
    }
    renderActionButtons();
    renderTable();
  });
 
  // initial render
  renderActionButtons();
  renderTable();
});