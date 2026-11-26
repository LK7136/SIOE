document.addEventListener('DOMContentLoaded', function () {
    const fileInput = document.getElementById('planTechnique');
    const errorDiv = document.getElementById('planTechniqueError');
    const previewContainer = document.getElementById('planPreviewContainer');
    const previewLabel = document.getElementById('planPreviewLabel');
    const previewFrame = document.getElementById('planPreviewFrame');

    if (!fileInput || !previewFrame) return;

    const initialSrc = previewFrame.dataset.initialSrc || '';

    function resetToInitialPreview() {
        if (previewFrame.dataset.blobUrl) {
            URL.revokeObjectURL(previewFrame.dataset.blobUrl);
            delete previewFrame.dataset.blobUrl;
        }
        if (initialSrc) {
            previewFrame.src = initialSrc;
            previewLabel.textContent = 'Aperçu du Plan Technique actuel';
            previewContainer.style.display = 'block';
        } else {
            previewFrame.src = 'about:blank';
            previewContainer.style.display = 'none';
        }
    }

    fileInput.addEventListener('change', function (e) {
        const file = e.target.files[0];
        errorDiv.style.display = 'none';
        errorDiv.textContent = '';

        if (!file) {
            resetToInitialPreview();
            return;
        }

        const isPdf = file.type === 'application/pdf' || file.name.toLowerCase().endsWith('.pdf');
        if (!isPdf) {
            errorDiv.textContent = 'Format invalide : seuls les fichiers PDF sont acceptés pour le plan technique.';
            errorDiv.style.display = 'block';
            e.target.value = '';
            resetToInitialPreview();
            return;
        }

        if (previewFrame.dataset.blobUrl) {
            URL.revokeObjectURL(previewFrame.dataset.blobUrl);
        }

        const blobUrl = URL.createObjectURL(file);
        previewFrame.src = blobUrl;
        previewFrame.dataset.blobUrl = blobUrl;
        previewLabel.textContent = 'Aperçu du nouveau Plan Technique sélectionné (non enregistré)';
        previewContainer.style.display = 'block';
    });
});