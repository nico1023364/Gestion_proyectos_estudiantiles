const CONTEXT_PATH = (() => {
    const path = window.location.pathname;
    if (path.endsWith('/index.html')) {
        return path.replace('/index.html', '');
    }
    return path === '/' ? '' : path.replace(/\/$/, '');
})();

const API = `${window.location.origin}${CONTEXT_PATH}/api`;

let estudiantes = [];
let docentes = [];
let proyectos = [];
let actividades = [];
let entregables = [];

const messageBox = document.getElementById('messageBox');

document.addEventListener('DOMContentLoaded', () => {
    configurarTabs();
    configurarFormularios();
    cargarTodo();
});

function configurarTabs() {
    document.querySelectorAll('.tab').forEach(tab => {
        tab.addEventListener('click', () => {
            document.querySelectorAll('.tab').forEach(btn => btn.classList.remove('active'));
            document.querySelectorAll('.content-section').forEach(section => section.classList.remove('active-section'));
            tab.classList.add('active');
            document.getElementById(tab.dataset.section).classList.add('active-section');
        });
    });
}

function configurarFormularios() {
    document.getElementById('estudianteForm').addEventListener('submit', guardarEstudiante);
    document.getElementById('docenteForm').addEventListener('submit', guardarDocente);
    document.getElementById('proyectoForm').addEventListener('submit', guardarProyecto);
    document.getElementById('actividadForm').addEventListener('submit', guardarActividad);
    document.getElementById('entregableForm').addEventListener('submit', guardarEntregable);
}

async function cargarTodo() {
    await Promise.all([
        cargarEstudiantes(),
        cargarDocentes(),
        cargarProyectos(),
        cargarActividades(),
        cargarEntregables()
    ]);
    actualizarResumen();
}

async function request(url, options = {}) {
    const response = await fetch(url, {
        headers: { 'Content-Type': 'application/json' },
        ...options
    });

    const contentType = response.headers.get('content-type') || '';
    const text = await response.text();
    let data = null;

    if (text && contentType.includes('application/json')) {
        data = JSON.parse(text);
    }

    if (!response.ok) {
        if (!contentType.includes('application/json')) {
            throw new Error(`El servidor respondió ${response.status} y no devolvió JSON. Revisa la URL del API y el log de Tomcat.`);
        }
        throw new Error(data?.error || 'Ocurrió un error inesperado.');
    }

    return data;
}

function showMessage(text, type = 'success') {
    messageBox.textContent = text;
    messageBox.className = `message ${type}`;
    messageBox.classList.remove('hidden');
    window.scrollTo({ top: 0, behavior: 'smooth' });
    setTimeout(() => messageBox.classList.add('hidden'), 3500);
}

function actualizarResumen() {
    document.getElementById('countEstudiantes').textContent = estudiantes.length;
    document.getElementById('countDocentes').textContent = docentes.length;
    document.getElementById('countProyectos').textContent = proyectos.length;
    document.getElementById('countActividades').textContent = actividades.length;
    document.getElementById('countEntregables').textContent = entregables.length;
}

function escapeHtml(value) {
    return String(value ?? '')
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#39;');
}

function optionPlaceholder(texto) {
    return `<option value="">${texto}</option>`;
}

function renderActions(editFn, deleteFn, id) {
    return `
        <div class="table-actions">
            <button type="button" onclick="${editFn}(${id})">Editar</button>
            <button type="button" class="delete-btn" onclick="${deleteFn}(${id})">Eliminar</button>
        </div>
    `;
}

async function cargarEstudiantes() {
    estudiantes = await request(`${API}/estudiantes`);
    document.getElementById('estudiantesTable').innerHTML = estudiantes.map(e => `
        <tr>
            <td>${e.id}</td>
            <td>${escapeHtml(e.nombre)}</td>
            <td>${escapeHtml(e.correo)}</td>
            <td>${escapeHtml(e.programaAcademico)}</td>
            <td>${renderActions('editarEstudiante', 'eliminarEstudiante', e.id)}</td>
        </tr>
    `).join('') || '<tr><td colspan="5">No hay estudiantes registrados.</td></tr>';

    document.getElementById('entregableEstudiante').innerHTML = optionPlaceholder('Seleccione un estudiante') +
        estudiantes.map(e => `<option value="${e.id}">${escapeHtml(e.nombre)}</option>`).join('');
    actualizarResumen();
}

async function guardarEstudiante(event) {
    event.preventDefault();
    const id = document.getElementById('estudianteId').value;
    const body = {
        nombre: document.getElementById('estudianteNombre').value,
        correo: document.getElementById('estudianteCorreo').value,
        programaAcademico: document.getElementById('estudiantePrograma').value
    };

    await request(id ? `${API}/estudiantes/${id}` : `${API}/estudiantes`, {
        method: id ? 'PUT' : 'POST',
        body: JSON.stringify(body)
    });

    showMessage(id ? 'Estudiante actualizado correctamente.' : 'Estudiante creado correctamente.');
    limpiarEstudianteForm();
    await Promise.all([cargarEstudiantes(), cargarEntregables()]);
}

window.editarEstudiante = function (id) {
    const e = estudiantes.find(item => item.id === id);
    document.getElementById('estudianteId').value = e.id;
    document.getElementById('estudianteNombre').value = e.nombre;
    document.getElementById('estudianteCorreo').value = e.correo;
    document.getElementById('estudiantePrograma').value = e.programaAcademico;
};

window.eliminarEstudiante = async function (id) {
    if (!confirm('¿Deseas eliminar este estudiante?')) return;
    try {
        await request(`${API}/estudiantes/${id}`, { method: 'DELETE' });
        showMessage('Estudiante eliminado correctamente.');
        await Promise.all([cargarEstudiantes(), cargarEntregables()]);
    } catch (error) {
        showMessage(error.message, 'error');
    }
};

window.limpiarEstudianteForm = function () {
    document.getElementById('estudianteForm').reset();
    document.getElementById('estudianteId').value = '';
};

async function cargarDocentes() {
    docentes = await request(`${API}/docentes`);
    document.getElementById('docentesTable').innerHTML = docentes.map(d => `
        <tr>
            <td>${d.id}</td>
            <td>${escapeHtml(d.nombre)}</td>
            <td>${escapeHtml(d.correo)}</td>
            <td>${escapeHtml(d.area)}</td>
            <td>${renderActions('editarDocente', 'eliminarDocente', d.id)}</td>
        </tr>
    `).join('') || '<tr><td colspan="5">No hay docentes registrados.</td></tr>';

    document.getElementById('proyectoDocente').innerHTML = optionPlaceholder('Seleccione un docente') +
        docentes.map(d => `<option value="${d.id}">${escapeHtml(d.nombre)}</option>`).join('');
    actualizarResumen();
}

async function guardarDocente(event) {
    event.preventDefault();
    const id = document.getElementById('docenteId').value;
    const body = {
        nombre: document.getElementById('docenteNombre').value,
        correo: document.getElementById('docenteCorreo').value,
        area: document.getElementById('docenteArea').value
    };

    await request(id ? `${API}/docentes/${id}` : `${API}/docentes`, {
        method: id ? 'PUT' : 'POST',
        body: JSON.stringify(body)
    });

    showMessage(id ? 'Docente actualizado correctamente.' : 'Docente creado correctamente.');
    limpiarDocenteForm();
    await Promise.all([cargarDocentes(), cargarProyectos()]);
}

window.editarDocente = function (id) {
    const d = docentes.find(item => item.id === id);
    document.getElementById('docenteId').value = d.id;
    document.getElementById('docenteNombre').value = d.nombre;
    document.getElementById('docenteCorreo').value = d.correo;
    document.getElementById('docenteArea').value = d.area;
};

window.eliminarDocente = async function (id) {
    if (!confirm('¿Deseas eliminar este docente?')) return;
    try {
        await request(`${API}/docentes/${id}`, { method: 'DELETE' });
        showMessage('Docente eliminado correctamente.');
        await Promise.all([cargarDocentes(), cargarProyectos()]);
    } catch (error) {
        showMessage(error.message, 'error');
    }
};

window.limpiarDocenteForm = function () {
    document.getElementById('docenteForm').reset();
    document.getElementById('docenteId').value = '';
};

async function cargarProyectos() {
    proyectos = await request(`${API}/proyectos`);
    document.getElementById('proyectosTable').innerHTML = proyectos.map(p => `
        <tr>
            <td>${p.id}</td>
            <td>
                <strong>${escapeHtml(p.nombre)}</strong><br>
                <small>${escapeHtml(p.descripcion)}</small>
            </td>
            <td>${escapeHtml(p.docente?.nombre || '')}</td>
            <td>${escapeHtml(p.fechaInicio)}</td>
            <td>${escapeHtml(p.fechaFin)}</td>
            <td>${renderActions('editarProyecto', 'eliminarProyecto', p.id)}</td>
        </tr>
    `).join('') || '<tr><td colspan="6">No hay proyectos registrados.</td></tr>';

    document.getElementById('actividadProyecto').innerHTML = optionPlaceholder('Seleccione un proyecto') +
        proyectos.map(p => `<option value="${p.id}">${escapeHtml(p.nombre)}</option>`).join('');
    actualizarResumen();
}

async function guardarProyecto(event) {
    event.preventDefault();
    const id = document.getElementById('proyectoId').value;
    const body = {
        nombre: document.getElementById('proyectoNombre').value,
        descripcion: document.getElementById('proyectoDescripcion').value,
        objetivos: document.getElementById('proyectoObjetivos').value,
        fechaInicio: document.getElementById('proyectoFechaInicio').value,
        fechaFin: document.getElementById('proyectoFechaFin').value,
        docente: { id: Number(document.getElementById('proyectoDocente').value) }
    };

    await request(id ? `${API}/proyectos/${id}` : `${API}/proyectos`, {
        method: id ? 'PUT' : 'POST',
        body: JSON.stringify(body)
    });

    showMessage(id ? 'Proyecto actualizado correctamente.' : 'Proyecto creado correctamente.');
    limpiarProyectoForm();
    await Promise.all([cargarProyectos(), cargarActividades()]);
}

window.editarProyecto = function (id) {
    const p = proyectos.find(item => item.id === id);
    document.getElementById('proyectoId').value = p.id;
    document.getElementById('proyectoNombre').value = p.nombre;
    document.getElementById('proyectoDescripcion').value = p.descripcion;
    document.getElementById('proyectoObjetivos').value = p.objetivos;
    document.getElementById('proyectoFechaInicio').value = p.fechaInicio;
    document.getElementById('proyectoFechaFin').value = p.fechaFin;
    document.getElementById('proyectoDocente').value = p.docente?.id || '';
};

window.eliminarProyecto = async function (id) {
    if (!confirm('¿Deseas eliminar este proyecto?')) return;
    try {
        await request(`${API}/proyectos/${id}`, { method: 'DELETE' });
        showMessage('Proyecto eliminado correctamente.');
        await Promise.all([cargarProyectos(), cargarActividades()]);
    } catch (error) {
        showMessage(error.message, 'error');
    }
};

window.limpiarProyectoForm = function () {
    document.getElementById('proyectoForm').reset();
    document.getElementById('proyectoId').value = '';
};

async function cargarActividades() {
    actividades = await request(`${API}/actividades`);
    document.getElementById('actividadesTable').innerHTML = actividades.map(a => `
        <tr>
            <td>${a.id}</td>
            <td>
                <strong>${escapeHtml(a.nombre)}</strong><br>
                <small>${escapeHtml(a.descripcion)}</small>
            </td>
            <td>${escapeHtml(a.proyecto?.nombre || '')}</td>
            <td>${escapeHtml(a.fechaEntrega)}</td>
            <td>${renderActions('editarActividad', 'eliminarActividad', a.id)}</td>
        </tr>
    `).join('') || '<tr><td colspan="5">No hay actividades registradas.</td></tr>';

    document.getElementById('entregableActividad').innerHTML = optionPlaceholder('Seleccione una actividad') +
        actividades.map(a => `<option value="${a.id}">${escapeHtml(a.nombre)}</option>`).join('');
    actualizarResumen();
}

async function guardarActividad(event) {
    event.preventDefault();
    const id = document.getElementById('actividadId').value;
    const body = {
        nombre: document.getElementById('actividadNombre').value,
        descripcion: document.getElementById('actividadDescripcion').value,
        fechaEntrega: document.getElementById('actividadFechaEntrega').value,
        proyecto: { id: Number(document.getElementById('actividadProyecto').value) }
    };

    await request(id ? `${API}/actividades/${id}` : `${API}/actividades`, {
        method: id ? 'PUT' : 'POST',
        body: JSON.stringify(body)
    });

    showMessage(id ? 'Actividad actualizada correctamente.' : 'Actividad creada correctamente.');
    limpiarActividadForm();
    await Promise.all([cargarActividades(), cargarEntregables()]);
}

window.editarActividad = function (id) {
    const a = actividades.find(item => item.id === id);
    document.getElementById('actividadId').value = a.id;
    document.getElementById('actividadNombre').value = a.nombre;
    document.getElementById('actividadDescripcion').value = a.descripcion;
    document.getElementById('actividadFechaEntrega').value = a.fechaEntrega;
    document.getElementById('actividadProyecto').value = a.proyecto?.id || '';
};

window.eliminarActividad = async function (id) {
    if (!confirm('¿Deseas eliminar esta actividad?')) return;
    try {
        await request(`${API}/actividades/${id}`, { method: 'DELETE' });
        showMessage('Actividad eliminada correctamente.');
        await Promise.all([cargarActividades(), cargarEntregables()]);
    } catch (error) {
        showMessage(error.message, 'error');
    }
};

window.limpiarActividadForm = function () {
    document.getElementById('actividadForm').reset();
    document.getElementById('actividadId').value = '';
};

async function cargarEntregables() {
    entregables = await request(`${API}/entregables`);
    document.getElementById('entregablesTable').innerHTML = entregables.map(e => `
        <tr>
            <td>${e.id}</td>
            <td>${escapeHtml(e.actividad?.nombre || '')}</td>
            <td>${escapeHtml(e.estudiante?.nombre || '')}</td>
            <td>${escapeHtml(e.estado)}</td>
            <td>${escapeHtml(e.fechaSubida)}</td>
            <td>${renderActions('editarEntregable', 'eliminarEntregable', e.id)}</td>
        </tr>
    `).join('') || '<tr><td colspan="6">No hay entregables registrados.</td></tr>';
    actualizarResumen();
}

async function guardarEntregable(event) {
    event.preventDefault();
    const id = document.getElementById('entregableId').value;
    const body = {
        actividad: { id: Number(document.getElementById('entregableActividad').value) },
        estudiante: { id: Number(document.getElementById('entregableEstudiante').value) },
        estado: document.getElementById('entregableEstado').value,
        fechaSubida: document.getElementById('entregableFechaSubida').value
    };

    await request(id ? `${API}/entregables/${id}` : `${API}/entregables`, {
        method: id ? 'PUT' : 'POST',
        body: JSON.stringify(body)
    });

    showMessage(id ? 'Entregable actualizado correctamente.' : 'Entregable creado correctamente.');
    limpiarEntregableForm();
    await cargarEntregables();
}

window.editarEntregable = function (id) {
    const e = entregables.find(item => item.id === id);
    document.getElementById('entregableId').value = e.id;
    document.getElementById('entregableActividad').value = e.actividad?.id || '';
    document.getElementById('entregableEstudiante').value = e.estudiante?.id || '';
    document.getElementById('entregableEstado').value = e.estado;
    document.getElementById('entregableFechaSubida').value = e.fechaSubida;
};

window.eliminarEntregable = async function (id) {
    if (!confirm('¿Deseas eliminar este entregable?')) return;
    try {
        await request(`${API}/entregables/${id}`, { method: 'DELETE' });
        showMessage('Entregable eliminado correctamente.');
        await cargarEntregables();
    } catch (error) {
        showMessage(error.message, 'error');
    }
};

window.limpiarEntregableForm = function () {
    document.getElementById('entregableForm').reset();
    document.getElementById('entregableId').value = '';
};

window.addEventListener('unhandledrejection', event => {
    showMessage(event.reason?.message || 'Ocurrió un error inesperado.', 'error');
});
