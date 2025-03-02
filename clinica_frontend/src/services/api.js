import axios from 'axios';
import { format, parseISO, addDays } from 'date-fns';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Función auxiliar para formatear fechas en formato MySQL
const formatDateForMySQL = (dateString) => {
  try {
    if (!dateString) return format(new Date(), 'yyyy-MM-dd');
    // Crear la fecha usando el string directamente sin parsearlo
    return dateString;
  } catch (error) {
    console.error('Error formatting date:', error);
    return format(new Date(), 'yyyy-MM-dd');
  }
};

// Función para ajustar la fecha recibida del servidor
const adjustDateFromServer = (dateString) => {
  try {
    if (!dateString) return '';
    // Crear la fecha usando el string directamente sin ajustes
    return dateString;
  } catch (error) {
    console.error('Error adjusting date from server:', error);
    return '';
  }
};

// Interceptor para transformar los datos antes de enviarlos al servidor
api.interceptors.request.use((config) => {
  if (config.data) {
    const transformedData = { ...config.data };
    console.log('Datos originales:', config.data);

    // Manejo de fechas
    if (transformedData.fecha_nacimiento) {
      transformedData.fechaNacimiento = formatDateForMySQL(transformedData.fecha_nacimiento);
      delete transformedData.fecha_nacimiento;
    }
    if (transformedData.fecha) {
      transformedData.fecha = formatDateForMySQL(transformedData.fecha);
    }

    // Asegurar que los IDs sean números válidos
    if ('paciente_id' in transformedData) {
      transformedData.paciente_id = Number(transformedData.paciente_id);
    }
    if ('medico_id' in transformedData) {
      transformedData.medico_id = Number(transformedData.medico_id);
    }
    if ('consultorio_id' in transformedData) {
      transformedData.consultorio_id = Number(transformedData.consultorio_id);
    }

    // Asegurar que la hora tenga el formato correcto (HH:mm:ss)
    if (transformedData.hora) {
      const hora = transformedData.hora.trim();
      if (hora.length === 5) { // formato HH:mm
        transformedData.hora = hora + ':00';
      } else if (!hora.match(/^\d{2}:\d{2}:\d{2}$/)) {
        console.error('Formato de hora inválido:', hora);
        throw new Error('El formato de hora debe ser HH:mm o HH:mm:ss');
      }
    }

    console.log('Datos transformados antes de enviar al servidor:', transformedData);
    config.data = transformedData;
  }
  return config;
}, (error) => {
  console.error('Error en el interceptor de request:', error);
  return Promise.reject(error);
});

// Interceptor para formatear fechas después de recibir del servidor
api.interceptors.response.use((response) => {
  if (response.data && typeof response.data === 'object') {
    console.log('Respuesta del servidor antes de transformar:', response.data);

    if ('timestamp' in response.data) {
      // Si es un objeto de error de Spring, extraer el mensaje completo
      const errorMessage = response.data.message || response.data.error || 'Error del servidor';
      console.error('Error del servidor:', response.data);
      return Promise.reject(new Error(errorMessage));
    }

    // Si es un array de pacientes, formatear las fechas
    if (Array.isArray(response.data)) {
      response.data = response.data.map(item => {
        if (item.fechaNacimiento) {
          return {
            ...item,
            fecha_nacimiento: adjustDateFromServer(item.fechaNacimiento)
          };
        }
        return item;
      });
    }
    // Si es un solo paciente
    else if (response.data.fechaNacimiento) {
      response.data = {
        ...response.data,
        fecha_nacimiento: adjustDateFromServer(response.data.fechaNacimiento)
      };
    }

    console.log('Respuesta transformada:', response.data);
  }
  return response;
}, (error) => {
  console.error('Error en la respuesta:', error);
  if (error.response?.data) {
    console.error('Datos del error:', error.response.data);
    if (typeof error.response.data === 'object') {
      const errorMessage = error.response.data.message || 
                         error.response.data.error || 
                         JSON.stringify(error.response.data);
      error.message = errorMessage;
    } else {
      error.message = String(error.response.data);
    }
  }
  return Promise.reject(error);
});

export const pacientesService = {
  getAll: () => api.get('/pacientes/findAll'),
  create: (data) => api.post('/pacientes/create', data),
  update: (data) => api.patch('/pacientes/update', data),
  delete: (id) => api.delete(`/pacientes/delete?id=${id}`),
};

export const medicosService = {
  getAll: () => api.get('/medicos/findAll'),
  create: (data) => api.post('/medicos/create', data),
  update: (id, data) => api.patch(`/medicos/update/${id}`, data),
  delete: (id) => api.delete(`/medicos/delete?id=${id}`),
};

export const citasService = {
  getAll: () => api.get('/citas/findAll'),
  create: (data) => {
    console.log('Creando cita con datos:', data);
    return api.post('/citas/create', data);
  },
  update: (data) => {
    console.log('Actualizando cita con datos:', data);
    return api.patch('/citas/update', data);
  },
  delete: (id) => api.delete(`/citas/delete?id=${id}`),
};

export const consultoriosService = {
  getAll: () => api.get('/consultorios/findAll'),
  create: (data) => api.post('/consultorios/create', data),
  update: (data) => api.patch('/consultorios/update', data),
  delete: (id) => api.delete(`/consultorios/delete?id=${id}`),
};

export default api; 