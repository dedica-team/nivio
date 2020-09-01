import API from './APIConfig';
import { CancelTokenSource } from 'axios';

export const get = async (route: string, cancelTokenSource?: CancelTokenSource) => {
  let data = null;

  const response = await API.get(route, {
    cancelToken: cancelTokenSource?.token,
  });
  data = response.data;

  return data;
};
