import API from './API';

export const getAllLandscapes = async () => {
  let landscapes;
  await API.get('/api/').then((res) => {
    landscapes = res.data;
  });

  return landscapes;
};

export const getLandscapeByIdentifier = async (identifier: String) => {
  let landscape = null;

  await API.get(`/api/${identifier}`).then((res) => {
    landscape = res.data;
  });

  return landscape;
};

export const getEvents = async () => {
  let events = null;
  await API.get('/events').then((res) => {
    return res.data;
  });

  return events;
};

export const getItemByTopic = async (topic: String) => {
  let item;
  await API.get(`/api/${topic}`).then((res) => {
    item = res.data;
  });

  return item;
};

export const getLandscapeLog = async (identifier: String) => {
  let log = null;
  await API.get(`/api/landscape/${identifier}/log`).then((res) => {
    log = res.data.messages;
  });
  return log;
};
