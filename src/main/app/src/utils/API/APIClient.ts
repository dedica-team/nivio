import API from './APIConfig';

export const get = async (route: string) => {
  let data = null;

  await API.get(route).then((res) => {
    data = res.data;
  });

  return data;
};
