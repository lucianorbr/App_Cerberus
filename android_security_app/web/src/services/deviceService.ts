import { api } from './api';

export interface Device {
  id: string;
  userId: string;
  name: string;
  deviceId: string;
  fcmToken: string;
  lastSeen: number;
  isActive: boolean;
  settings: DeviceSettings;
}

export interface DeviceSettings {
  locationTrackingEnabled: boolean;
  wrongPasswordPhotoEnabled: boolean;
  simChangeDetectionEnabled: boolean;
  remoteLockEnabled: boolean;
  soundAlertEnabled: boolean;
  callControlEnabled: boolean;
  notificationEmail: string;
}

export interface LocationData {
  id: string;
  deviceId: string;
  latitude: number;
  longitude: number;
  accuracy: number;
  timestamp: number;
}

export interface Command {
  id: string;
  deviceId: string;
  type: CommandType;
  parameters: Record<string, string>;
  timestamp: number;
  status: CommandStatus;
}

export enum CommandType {
  LOCK_DEVICE = 'LOCK_DEVICE',
  RESET_PASSWORD = 'RESET_PASSWORD',
  SOUND_ALERT = 'SOUND_ALERT',
  STOP_SOUND_ALERT = 'STOP_SOUND_ALERT',
  MAKE_CALL = 'MAKE_CALL',
  TAKE_PHOTO = 'TAKE_PHOTO',
  UPDATE_SETTINGS = 'UPDATE_SETTINGS',
  WIPE_DATA = 'WIPE_DATA'
}

export enum CommandStatus {
  PENDING = 'PENDING',
  SENT = 'SENT',
  DELIVERED = 'DELIVERED',
  EXECUTED = 'EXECUTED',
  FAILED = 'FAILED'
}

// Obter todos os dispositivos do usuário
export const getDevices = async (): Promise<Device[]> => {
  const response = await api.get('/api/devices');
  return response.data;
};

// Obter um dispositivo específico
export const getDevice = async (deviceId: string): Promise<Device> => {
  const response = await api.get(`/api/devices/${deviceId}`);
  return response.data;
};

// Atualizar um dispositivo
export const updateDevice = async (deviceId: string, device: Partial<Device>): Promise<Device> => {
  const response = await api.put(`/api/devices/${deviceId}`, device);
  return response.data;
};

// Excluir um dispositivo
export const deleteDevice = async (deviceId: string): Promise<void> => {
  await api.delete(`/api/devices/${deviceId}`);
};

// Obter histórico de localização de um dispositivo
export const getDeviceLocations = async (deviceId: string): Promise<LocationData[]> => {
  const response = await api.get(`/api/devices/${deviceId}/locations`);
  return response.data;
};

// Enviar comando para o dispositivo
export const sendCommand = async (
  deviceId: string, 
  commandType: CommandType, 
  parameters: Record<string, string> = {}
): Promise<boolean> => {
  const response = await api.post(`/api/devices/${deviceId}/commands`, {
    command: commandType,
    ...parameters
  });
  return response.data.success;
};

// Bloquear dispositivo
export const lockDevice = async (deviceId: string): Promise<boolean> => {
  return sendCommand(deviceId, CommandType.LOCK_DEVICE);
};

// Alterar senha do dispositivo
export const resetPassword = async (deviceId: string, newPassword: string): Promise<boolean> => {
  return sendCommand(deviceId, CommandType.RESET_PASSWORD, { newPassword });
};

// Ativar alerta sonoro
export const playSoundAlert = async (deviceId: string): Promise<boolean> => {
  return sendCommand(deviceId, CommandType.SOUND_ALERT);
};

// Desativar alerta sonoro
export const stopSoundAlert = async (deviceId: string): Promise<boolean> => {
  return sendCommand(deviceId, CommandType.STOP_SOUND_ALERT);
};

// Fazer chamada telefônica
export const makeCall = async (deviceId: string, phoneNumber: string): Promise<boolean> => {
  return sendCommand(deviceId, CommandType.MAKE_CALL, { phoneNumber });
};

// Capturar foto
export const takePhoto = async (deviceId: string): Promise<boolean> => {
  return sendCommand(deviceId, CommandType.TAKE_PHOTO);
};

// Atualizar configurações do dispositivo
export const updateDeviceSettings = async (deviceId: string, settings: Partial<DeviceSettings>): Promise<boolean> => {
  return sendCommand(deviceId, CommandType.UPDATE_SETTINGS, settings as Record<string, string>);
};

// Limpar dados do dispositivo (factory reset)
export const wipeDeviceData = async (deviceId: string): Promise<boolean> => {
  return sendCommand(deviceId, CommandType.WIPE_DATA);
};
