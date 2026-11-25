document.addEventListener('DOMContentLoaded', function () {
    const listeWidgets = document.getElementById('liste-widgets');
    const form = document.getElementById('form-widgets');
    const checkboxes = document.querySelectorAll('#liste-widgets input[type="checkbox"][name="widgets"]');
    const saveBtn = document.getElementById('btn-save-widgets');
    const errorMsg = document.getElementById('widgets-error-msg');

    // 1. Initialisation du glisser-déposer (SortableJS)
    if (listeWidgets && typeof Sortable !== 'undefined') {
        new Sortable(listeWidgets, {
            handle: '.poignee',
            animation: 150
        });
    }

    // 2. Validation dynamique : au moins un widget doit rester coché
    function updateWidgetsValidation() {
        const checkedCount = Array.from(checkboxes).filter(cb => cb.checked).length;
        const hasAtLeastOne = checkedCount > 0;

        if (errorMsg) {
            errorMsg.style.display = hasAtLeastOne ? 'none' : 'block';
        }
        if (saveBtn) {
            saveBtn.disabled = !hasAtLeastOne;
        }

        return hasAtLeastOne;
    }

    checkboxes.forEach(function (checkbox) {
        checkbox.addEventListener('change', updateWidgetsValidation);
    });

    if (form) {
        form.addEventListener('submit', function (e) {
            if (!updateWidgetsValidation()) {
                e.preventDefault();
            }
        });
    }

    // Vérification initiale au chargement de la page
    updateWidgetsValidation();
});