function search(query) {
  const pattern = new RegExp(query, "i");
  for (const row of document.querySelectorAll("#movies>tbody tr")) {
    if (pattern.test(row.querySelector("a").text)) {
      row.style.display = "";
    } else {
      row.style.display = "none";
    }
  }
}
