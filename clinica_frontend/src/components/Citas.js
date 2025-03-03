import React, { useState, useEffect } from 'react';
import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  IconButton,
  Typography,
  Alert,
  Snackbar,
  MenuItem,
} from '@mui/material';
import { Add as AddIcon, Edit as EditIcon, Delete as DeleteIcon } from '@mui/icons-material';
import { citasService, pacientesService, medicosService, consultoriosService } from '../services/api';
import { format, parseISO, addDays } from 'date-fns';

const formatDisplayDate = (dateString) => {
  try {
    if (!dateString) return '';
    // Crear una fecha usando el string y ajustando a la zona horaria local
    const date = new Date(dateString + 'T00:00:00');
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
  } catch (error) {
    console.error('Error formatting display date:', error);
    return '';
  }
};

function Citas() {
  const [citas, setCitas] = useState([]);
  const [pacientes, setPacientes] = useState([]);
  const [medicos, setMedicos] = useState([]);
  const [consultorios, setConsultorios] = useState([]);
  const [open, setOpen] = useState(false);
  const [selectedCita, setSelectedCita] = useState(null);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
  const [formData, setFormData] = useState({
    paciente_id: '',
    medico_id: '',
    fecha: format(new Date(), 'yyyy-MM-dd'),
    hora: '08:00',
    consultorio_id: '',
  });

  const generateTimeOptions = () => {
    const options = [];
    const now = new Date();
    const isToday = formData.fecha === format(now, 'yyyy-MM-dd');
    
    // Si es hoy, comenzar desde la hora actual
    let startHour = 8;
    let startMinutes = 0;
    
    if (isToday) {
      startHour = now.getHours();
      startMinutes = now.getMinutes();
      
      // Ajustar al siguiente intervalo de 30 minutos
      if (startMinutes > 30) {
        startHour += 1;
        startMinutes = 0;
      } else if (startMinutes > 0) {
        startMinutes = 30;
      }
      
      // Si ya pasó la última hora disponible, no mostrar opciones
      if (startHour >= 22 || (startHour === 21 && startMinutes > 30)) {
        return [];
      }
      
      // Asegurarse de no comenzar antes de las 8:00
      if (startHour < 8) {
        startHour = 8;
        startMinutes = 0;
      }
    }
    
    // Generar las opciones de tiempo
    for (let hour = startHour; hour < 22; hour++) {
      for (let minutes = (hour === startHour ? startMinutes : 0); minutes < 60; minutes += 30) {
        // No incluir 22:00
        if (hour === 21 && minutes > 30) continue;
        
        const formattedHour = String(hour).padStart(2, '0');
        const formattedMinutes = String(minutes).padStart(2, '0');
        options.push(`${formattedHour}:${formattedMinutes}`);
      }
    }
    
    return options;
  };

  const loadData = async () => {
    try {
      const [citasRes, pacientesRes, medicosRes, consultoriosRes] = await Promise.all([
        citasService.getAll(),
        pacientesService.getAll(),
        medicosService.getAll(),
        consultoriosService.getAll(),
      ]);
      
      console.log('Datos cargados:', {
        citas: citasRes.data,
        pacientes: pacientesRes.data,
        medicos: medicosRes.data,
        consultorios: consultoriosRes.data
      });

      // Asegurarse de que los arrays sean válidos y contienen datos válidos
      const citasData = Array.isArray(citasRes.data) ? citasRes.data.filter(cita => cita && typeof cita === 'object') : [];
      const pacientesData = Array.isArray(pacientesRes.data) ? pacientesRes.data.filter(paciente => paciente && typeof paciente === 'object') : [];
      const medicosData = Array.isArray(medicosRes.data) ? medicosRes.data.filter(medico => medico && typeof medico === 'object') : [];
      const consultoriosData = Array.isArray(consultoriosRes.data) ? consultoriosRes.data.filter(consultorio => consultorio && typeof consultorio === 'object') : [];

      console.log('Médicos procesados:', medicosData);

      setCitas(citasData);
      setPacientes(pacientesData);
      setMedicos(medicosData);
      setConsultorios(consultoriosData);
    } catch (error) {
      console.error('Error al cargar datos:', error);
      showSnackbar('Error al cargar los datos', 'error');
      setCitas([]);
      setPacientes([]);
      setMedicos([]);
      setConsultorios([]);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleOpen = (cita = null) => {
    if (cita) {
      console.log('Abriendo cita para editar:', cita);
      setSelectedCita(cita);
      setFormData({
        paciente_id: String(cita.paciente_id || ''),
        medico_id: String(cita.medico_id || ''),
        fecha: cita.fecha ? cita.fecha.split('T')[0] : format(new Date(), 'yyyy-MM-dd'),
        hora: cita.hora || '08:00',
        consultorio_id: String(cita.consultorio_id || ''),
      });
    } else {
      setSelectedCita(null);
      setFormData({
        paciente_id: '',
        medico_id: '',
        fecha: format(new Date(), 'yyyy-MM-dd'),
        hora: '08:00',
        consultorio_id: '',
      });
    }
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setSelectedCita(null);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => {
      const newFormData = {
        ...prev,
        [name]: value,
      };

      // Si cambia la fecha, validar la hora seleccionada
      if (name === 'fecha') {
        const now = new Date();
        const isToday = value === format(now, 'yyyy-MM-dd');
        
        if (isToday) {
          // Obtener las horas disponibles para hoy
          const availableHours = generateTimeOptions();
          
          // Si no hay horas disponibles o la hora actual no está en las opciones disponibles
          if (availableHours.length === 0 || !availableHours.includes(prev.hora)) {
            // Seleccionar la primera hora disponible o limpiar la selección
            newFormData.hora = availableHours.length > 0 ? availableHours[0] : '';
          }
        }
      }

      return newFormData;
    });
  };

  const showSnackbar = (message, severity = 'success') => {
    setSnackbar({ open: true, message, severity });
  };

  const handleSnackbarClose = () => {
    setSnackbar({ ...snackbar, open: false });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // Validar que todos los campos requeridos estén presentes y sean válidos
      const validationErrors = [];
      
      if (!formData.paciente_id) {
        validationErrors.push('Debe seleccionar un paciente');
      }
      if (!formData.medico_id) {
        validationErrors.push('Debe seleccionar un médico');
      }
      if (!formData.consultorio_id) {
        validationErrors.push('Debe seleccionar un consultorio');
      }
      if (!formData.fecha) {
        validationErrors.push('Debe seleccionar una fecha');
      }
      if (!formData.hora) {
        validationErrors.push('Debe seleccionar una hora');
      }

      // Validar que la fecha no sea anterior a la fecha actual
      const now = new Date();
      now.setHours(0, 0, 0, 0);
      const fechaCita = new Date(formData.fecha + 'T00:00:00');

      if (fechaCita < now) {
        validationErrors.push('La fecha de la cita no puede ser anterior a la fecha actual');
      }

      // Si es el mismo día, validar la hora
      if (format(fechaCita, 'yyyy-MM-dd') === format(new Date(), 'yyyy-MM-dd')) {
        const [horaStr, minutosStr] = formData.hora.split(':');
        const horaActual = new Date();
        const horaCita = new Date();
        horaCita.setHours(parseInt(horaStr, 10), parseInt(minutosStr, 10), 0, 0);

        // Asegurar que haya al menos 30 minutos de diferencia
        const diffMinutes = Math.floor((horaCita - horaActual) / (1000 * 60));
        if (diffMinutes < 30) {
          validationErrors.push('Debe haber al menos 30 minutos de diferencia entre la hora actual y la hora de la cita');
        }
      }

      if (validationErrors.length > 0) {
        showSnackbar(validationErrors.join('. '), 'error');
        return;
      }

      // Convertir los IDs a números y asegurar que todos los campos necesarios estén presentes
      const citaData = {
        ...(selectedCita?.id && { id: selectedCita.id }),
        paciente_id: Number(formData.paciente_id),
        medico_id: Number(formData.medico_id),
        consultorio_id: Number(formData.consultorio_id),
        fecha: formData.fecha,
        fecha: formData.fecha, // Mantener la fecha en formato YYYY-MM-DD
        hora: formData.hora.length === 5 ? formData.hora + ':00' : formData.hora
      };

      console.log('Enviando datos de cita:', citaData);

      let response;
      if (selectedCita) {
        console.log('Actualizando cita existente con ID:', selectedCita.id);
        response = await citasService.update(citaData);
        console.log('Respuesta de actualización:', response);
        showSnackbar('Cita actualizada correctamente');
      } else {
        console.log('Creando nueva cita');
        response = await citasService.create(citaData);
        console.log('Respuesta de creación:', response);
        showSnackbar('Cita creada correctamente');
      }
      
      handleClose();
      await loadData();
    } catch (error) {
      console.error('Error al guardar cita:', error);
      const errorMessage = error.response?.data || error.message || 'Error al guardar la cita';
      showSnackbar(errorMessage, 'error');
    }
  };

  const handleDelete = async (id) => {
    if (!id) {
      showSnackbar('ID de cita inválido', 'error');
      return;
    }

    try {
      const confirmDelete = window.confirm('¿Está seguro de eliminar esta cita?');
      if (confirmDelete) {
        await citasService.delete(id);
        showSnackbar('Cita eliminada correctamente');
        // Recargar los datos inmediatamente después de eliminar
        await loadData();
      }
    } catch (error) {
      console.error('Error al eliminar cita:', error);
      const errorMessage = error.response?.data || error.message || 'Error al eliminar la cita';
      showSnackbar(errorMessage, 'error');
    }
  };

  const getPacienteName = (id) => {
    if (!id) return 'No encontrado';
    const paciente = pacientes.find(p => p?.id === Number(id));
    return paciente ? `${paciente.nombre || ''} ${paciente.apellido || ''}`.trim() || 'Sin nombre' : 'No encontrado';
  };

  const getMedicoName = (id) => {
    if (!id) return 'No encontrado';
    const medico = medicos.find(m => m?.id === Number(id));
    if (!medico) {
      console.log('Médico no encontrado para id:', id);
      console.log('Médicos disponibles:', medicos);
      return 'No encontrado';
    }
    return `${medico.nombre || ''} ${medico.apellido || ''} - ${medico.especialidad || ''}`.trim() || 'Sin nombre';
  };

  const getConsultorioInfo = (id) => {
    if (!id) return 'No encontrado';
    const consultorio = consultorios.find(c => c?.id === Number(id));
    return consultorio ? `Consultorio ${consultorio.numero} - Piso ${consultorio.piso}` : 'No encontrado';
  };

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" component="h1" gutterBottom>
          Citas
        </Typography>
        <Button
          variant="contained"
          color="primary"
          startIcon={<AddIcon />}
          onClick={() => handleOpen()}
        >
          Nueva Cita
        </Button>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Paciente</TableCell>
              <TableCell>Médico</TableCell>
              <TableCell>Fecha</TableCell>
              <TableCell>Hora</TableCell>
              <TableCell>Consultorio</TableCell>
              <TableCell align="right">Acciones</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {Array.isArray(citas) && citas.map((cita) => (
              <TableRow key={cita?.id || Math.random()}>
                <TableCell>{getPacienteName(cita?.paciente_id)}</TableCell>
                <TableCell>{getMedicoName(cita?.medico_id)}</TableCell>
                <TableCell>{formatDisplayDate(cita?.fecha)}</TableCell>
                <TableCell>{cita?.hora || ''}</TableCell>
                <TableCell>{getConsultorioInfo(cita?.consultorio_id)}</TableCell>
                <TableCell align="right">
                  <IconButton color="primary" onClick={() => handleOpen(cita)}>
                    <EditIcon />
                  </IconButton>
                  <IconButton color="error" onClick={() => handleDelete(cita?.id)}>
                    <DeleteIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
            {(!Array.isArray(citas) || citas.length === 0) && (
              <TableRow>
                <TableCell colSpan={6} align="center">
                  No hay citas registradas
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
        <DialogTitle>
          {selectedCita ? 'Editar Cita' : 'Nueva Cita'}
        </DialogTitle>
        <DialogContent>
          <Box component="form" onSubmit={handleSubmit} sx={{ mt: 2 }}>
            <TextField
              select
              fullWidth
              margin="normal"
              label="Paciente"
              name="paciente_id"
              value={formData.paciente_id}
              onChange={handleInputChange}
              required
            >
              <MenuItem value="">
                <em>Seleccione un paciente</em>
              </MenuItem>
              {Array.isArray(pacientes) && pacientes.map((paciente) => (
                <MenuItem key={paciente?.id || Math.random()} value={paciente?.id?.toString() || ''}>
                  {`${paciente?.nombre || ''} ${paciente?.apellido || ''}`.trim() || 'Sin nombre'}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              select
              fullWidth
              margin="normal"
              label="Médico"
              name="medico_id"
              value={formData.medico_id}
              onChange={handleInputChange}
              required
            >
              <MenuItem value="">
                <em>Seleccione un médico</em>
              </MenuItem>
              {Array.isArray(medicos) && medicos.map((medico) => (
                <MenuItem key={medico?.id || Math.random()} value={medico?.id?.toString() || ''}>
                  {`${medico?.nombre || ''} ${medico?.apellido || ''} - ${medico?.especialidad || ''}`.trim() || 'Sin nombre'}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              fullWidth
              margin="normal"
              label="Fecha"
              name="fecha"
              type="date"
              value={formData.fecha}
              onChange={handleInputChange}
              InputLabelProps={{ shrink: true }}
              required
              helperText="Solo se permiten citas desde la fecha actual en adelante"
              inputProps={{
                min: format(new Date(), 'yyyy-MM-dd'),
                max: format(addDays(new Date(), 365), 'yyyy-MM-dd')
              }}
            />
            <TextField
              select
              fullWidth
              margin="normal"
              label="Hora"
              name="hora"
              value={formData.hora}
              onChange={handleInputChange}
              required
              helperText={
                formData.fecha === format(new Date(), 'yyyy-MM-dd')
                  ? "Solo se muestran los horarios disponibles para hoy"
                  : "Horarios disponibles de 8:00 a 21:30"
              }
            >
              {generateTimeOptions().map((time) => (
                <MenuItem key={time} value={time}>
                  {time}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              select
              fullWidth
              margin="normal"
              label="Consultorio"
              name="consultorio_id"
              value={formData.consultorio_id}
              onChange={handleInputChange}
              required
            >
              <MenuItem value="">
                <em>Seleccione un consultorio</em>
              </MenuItem>
              {Array.isArray(consultorios) && consultorios.map((consultorio) => (
                <MenuItem key={consultorio?.id || Math.random()} value={consultorio?.id?.toString() || ''}>
                  {`Consultorio ${consultorio?.numero || ''} - Piso ${consultorio?.piso || ''}`.trim() || 'Sin información'}
                </MenuItem>
              ))}
            </TextField>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancelar</Button>
          <Button onClick={handleSubmit} variant="contained" color="primary">
            {selectedCita ? 'Actualizar' : 'Crear'}
          </Button>
        </DialogActions>
      </Dialog>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={handleSnackbarClose}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      >
        <Alert onClose={handleSnackbarClose} severity={snackbar.severity}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
}

export default Citas; 