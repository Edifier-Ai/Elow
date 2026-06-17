const catalogRows = document.querySelector("#catalogRows");
const userRows = document.querySelector("#userRows");
const recordRows = document.querySelector("#recordRows");
const saveStatus = document.querySelector("#saveStatus");
const refreshButton = document.querySelector("#refreshButton");

const fieldNames = [
  "displayName",
  "family",
  "fullServingLabel",
  "fullServingMl",
  "fullServingSugarGrams",
  "fullServingAlcoholGrams",
  "fullServingCalories",
  "fullServingMoney",
  "imageUrl",
];

refreshButton.addEventListener("click", loadOverview);

async function loadOverview() {
  saveStatus.textContent = "";
  const response = await fetch("/api/admin/overview");
  const overview = await response.json();
  renderCatalog(overview.catalog || []);
  renderUsers(overview.users || []);
  renderRecords(overview.records || []);
}

function renderCatalog(items) {
  catalogRows.innerHTML = "";
  for (const item of items) {
    const row = document.createElement("tr");
    row.dataset.type = item.type;
    row.innerHTML = `
      <td class="itemCell">
        <strong>${item.type}</strong>
        <input data-field="displayName" value="${escapeAttr(item.displayName)}" />
      </td>
      <td>
        <select data-field="family">
          <option value="SUGAR"${item.family === "SUGAR" ? " selected" : ""}>SUGAR</option>
          <option value="ALCOHOL"${item.family === "ALCOHOL" ? " selected" : ""}>ALCOHOL</option>
        </select>
      </td>
      <td>
        <input data-field="fullServingLabel" value="${escapeAttr(item.fullServingLabel)}" />
        <input data-field="fullServingMl" type="number" min="0" step="1" value="${item.fullServingMl}" />
      </td>
      <td><input data-field="fullServingSugarGrams" type="number" min="0" step="0.1" value="${item.fullServingSugarGrams}" /></td>
      <td><input data-field="fullServingAlcoholGrams" type="number" min="0" step="0.1" value="${item.fullServingAlcoholGrams}" /></td>
      <td><input data-field="fullServingCalories" type="number" min="0" step="1" value="${item.fullServingCalories}" /></td>
      <td><input data-field="fullServingMoney" type="number" min="0" step="0.01" value="${item.fullServingMoney}" /></td>
      <td class="imageCell"><input data-field="imageUrl" value="${escapeAttr(item.imageUrl || "")}" /></td>
      <td><button type="button" data-action="save">Save</button></td>
    `;
    row.querySelector("[data-action='save']").addEventListener("click", () => saveCatalogRow(row));
    catalogRows.appendChild(row);
  }
}

async function saveCatalogRow(row) {
  const type = row.dataset.type;
  const payload = {};
  for (const field of fieldNames) {
    const input = row.querySelector(`[data-field='${field}']`);
    if (!input) continue;
    const numeric = field.startsWith("fullServing") && field !== "fullServingLabel";
    payload[field] = numeric ? Number(input.value) : input.value;
  }
  payload.enabled = true;

  saveStatus.textContent = `Saving ${type}...`;
  const response = await fetch(`/api/admin/catalog/${type}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });
  if (!response.ok) {
    const error = await response.json().catch(() => ({ error: "Unknown error" }));
    saveStatus.innerHTML = `<span class="dangerText">${error.error}</span>`;
    return;
  }
  saveStatus.textContent = `${type} saved`;
  await loadOverview();
}

function renderUsers(users) {
  userRows.innerHTML = "";
  if (users.length === 0) {
    userRows.innerHTML = `<div class="empty">No users yet.</div>`;
    return;
  }
  for (const user of users) {
    const card = document.createElement("div");
    card.className = "rowCard";
    card.innerHTML = `
      <div>
        <div class="title">${escapeText(user.userId)}</div>
        <div class="meta">Sweet ${user.goals.weeklySweetDrinkLimit}/week · Alcohol ${user.goals.weeklyAlcoholOccasionLimit}/week · Sugar ${user.goals.dailySugarGramTarget}g/day</div>
      </div>
      <span>${user.onboardingComplete ? "Onboarded" : "Intro"}</span>
    `;
    userRows.appendChild(card);
  }
}

function renderRecords(records) {
  recordRows.innerHTML = "";
  if (records.length === 0) {
    recordRows.innerHTML = `<div class="empty">No records yet.</div>`;
    return;
  }
  for (const record of records) {
    const card = document.createElement("div");
    card.className = "rowCard";
    const date = new Date(record.timestampEpochMillis);
    card.innerHTML = `
      <div>
        <div class="title">${record.itemType} · ${Math.round(record.amountFraction * 100)}%</div>
        <div class="meta">${escapeText(record.userId || "")} · ${date.toLocaleString()}</div>
      </div>
      <span>${record.metrics.sugarGrams.toFixed(1)}g sugar · ${record.metrics.alcoholGrams.toFixed(1)}g alcohol</span>
    `;
    recordRows.appendChild(card);
  }
}

function escapeAttr(value) {
  return String(value ?? "")
    .replace(/&/g, "&amp;")
    .replace(/"/g, "&quot;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;");
}

function escapeText(value) {
  return String(value ?? "")
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;");
}

loadOverview().catch((error) => {
  saveStatus.innerHTML = `<span class="dangerText">${error.message}</span>`;
});

