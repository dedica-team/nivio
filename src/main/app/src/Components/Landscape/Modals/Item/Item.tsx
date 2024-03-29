import React, { ReactElement, useContext, useEffect, useState } from 'react';
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  AppBar,
  Card,
  CardHeader,
  Link,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Tab,
  Table,
  TableBody,
  TableCell,
  TableRow,
  Tabs,
  Theme,
  Typography,
  useTheme,
} from '@material-ui/core';
import { get } from '../../../../utils/API/APIClient';
import CardContent from '@material-ui/core/CardContent';
import { IAssessmentProps, IItem } from '../../../../interfaces';
import { getItem, getLabelsWithPrefix, getMappedLabels } from '../../Utils/utils';
import StatusChip from '../../../StatusChip/StatusChip';
import IconButton from '@material-ui/core/IconButton';
import {
  Close,
  Details,
  ExpandMore,
  Info,
  InfoOutlined,
  MoreVertSharp,
  Power,
} from '@material-ui/icons';
import Chip from '@material-ui/core/Chip';
import { createStyles, makeStyles } from '@material-ui/core/styles';
import { LocateFunctionContext } from '../../../../Context/LocateFunctionContext';
import componentStyles from '../../../../Resources/styling/ComponentStyles';
import ItemAvatar from './ItemAvatar';
import { LandscapeContext } from '../../../../Context/LandscapeContext';
import { a11yProps, TabPanel } from '../../Utils/TabUtils';
import MappedString from '../../Utils/MappedString';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    labels: {
      backgroundColor: theme.palette.primary.main,
    },
    tag: {
      backgroundColor: theme.palette.primary.dark,
      padding: 0,
      fontSize: '0.7rem',
      height: 16,
    },
    interfaces: {
      backgroundColor: theme.palette.primary.main,
    },
  })
);

interface Props {
  small?: boolean;
  sticky?: boolean;
  fullyQualifiedItemIdentifier?: string;
}

/**
 * Returns a chosen Landscape item if information is available
 *
 *
 */
const Item: React.FC<Props> = ({ fullyQualifiedItemIdentifier, small, sticky }) => {
  const [item, setItem] = useState<IItem | undefined>(undefined);
  const [compact, setCompact] = useState<boolean>(false);
  const [visible, setVisible] = useState<boolean>(true);
  const [value, setValue] = React.useState(0);
  const locateFunctionContext = useContext(LocateFunctionContext);
  const landscapeContext = useContext(LandscapeContext);

  const classes = componentStyles();
  const extraClasses = useStyles();
  let inboundRelations: ReactElement[] = [];
  let outboundRelations: ReactElement[] = [];
  const theme = useTheme();

  const getInterfaces = (element: IItem): ReactElement | null => {
    if (!element?.interfaces) return null;
    let ifaceElements: ReactElement[] = [];
    element.interfaces.forEach((iface, key) => {
      ifaceElements.push(
        <Accordion key={key} className={extraClasses.interfaces}>
          <AccordionSummary
            expandIcon={<ExpandMore />}
            aria-controls={'panel_ifaces' + key + 'bh-content'}
            id={'panel_ifaces' + key + 'bh-header'}
          >
            <span
              title={iface.name || iface.path}
              style={{
                width: 200,
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
                paddingLeft: 10,
              }}
            >
              {iface.name || iface.path}
            </span>
          </AccordionSummary>
          <AccordionDetails>
            {iface.summary ? (
              <div>
                {iface.summary}
                <br />
                <br />
              </div>
            ) : null}
            {iface.description ? (
              <div>
                {iface.description}
                <br />
                <br />
              </div>
            ) : null}
            <List>
              <ListItemText
                aria-multiline={true}
                style={{
                  wordBreak: 'break-word',
                  overflowWrap: 'break-word',
                  wordWrap: 'break-word',
                  whiteSpace: 'normal',
                }}
              >
                Path: {iface.path || '-'}
              </ListItemText>
              <ListItemText
                aria-multiline={true}
                style={{
                  wordBreak: 'break-word',
                  overflowWrap: 'break-word',
                  wordWrap: 'break-word',
                  whiteSpace: 'normal',
                }}
              >
                Params: {iface.parameters || '-'}
              </ListItemText>
              <ListItemText
                aria-multiline={true}
                style={{
                  wordBreak: 'break-word',
                  overflowWrap: 'break-word',
                  wordWrap: 'break-word',
                  whiteSpace: 'normal',
                }}
              >
                Format: {iface.format || '-'}
              </ListItemText>
              <ListItemText
                aria-multiline={true}
                style={{
                  wordBreak: 'break-word',
                  overflowWrap: 'break-word',
                  wordWrap: 'break-word',
                  whiteSpace: 'normal',
                }}
              >
                Payload: {iface.payload || '-'}
              </ListItemText>
              <ListItemText
                aria-multiline={true}
                style={{
                  wordBreak: 'break-word',
                  overflowWrap: 'break-word',
                  wordWrap: 'break-word',
                  whiteSpace: 'normal',
                }}
              >
                Protection: {iface.protection || '-'}
              </ListItemText>
              <ListItemText
                aria-multiline={true}
                style={{
                  wordBreak: 'break-word',
                  overflowWrap: 'break-word',
                  wordWrap: 'break-word',
                  whiteSpace: 'normal',
                }}
              >
                Deprecated: {iface.deprecated ? 'Yes' : '-'}
              </ListItemText>
            </List>
          </AccordionDetails>
        </Accordion>
      );
    });

    if (ifaceElements.length === 0) return null;
    return <List dense={true}>{ifaceElements}</List>;
  };

  useEffect(() => {
    if (fullyQualifiedItemIdentifier) {
      get(`/api/${fullyQualifiedItemIdentifier}`)
        .then((loaded) => {
          setItem(loaded);
        })
        .catch((error) => {
          // Make the component invisible if api can't find an item
          if (error.response.status === 404) {
            setVisible(false);
          }
        });
    }
  }, [landscapeContext.landscape, fullyQualifiedItemIdentifier]);

  useEffect(() => {
    if (small) {
      setCompact(true);
    }
  }, [small]);

  if (item && landscapeContext.landscape) {
    for (let key of Object.keys(item.relations)) {
      let relation = item.relations[key];
      const isInbound = relation.direction === 'inbound';
      const primary = `${relation.name}`;
      let secondary = `${relation.description || ''} ${
        relation.type ? '(' + relation.type + ')' : ''
      }`;
      if (relation.format) secondary += ', format: ' + relation.format;
      let other = getItem(
        landscapeContext.landscape,
        isInbound ? relation.source : relation.target
      );
      if (!other) continue;
      const status = landscapeContext.getAssessmentSummary(other.fullyQualifiedIdentifier);
      const listItem = (
        <ListItem key={relation.name}>
          <ListItemIcon>
            <IconButton
              onClick={() => {
                if (locateFunctionContext.locateFunction) {
                  locateFunctionContext.locateFunction(
                    isInbound ? relation.source : relation.target
                  );
                }
              }}
              size={'small'}
              title={'Click to locate'}
            >
              <ItemAvatar item={other} statusColor={status?.status || ''} />
            </IconButton>
          </ListItemIcon>
          <ListItemText
            data-testid={'testInfoIcon'}
            primary={primary}
            secondary={
              <>
                <span>{secondary}</span>
                <span
                  title={
                    'A PROVIDER relation is a hard dependency that is required. A DATAFLOW relation is a soft dependency.'
                  }
                >
                  <InfoOutlined
                    style={{ color: theme.palette.info.main }}
                    fontSize='small'
                    data-testid={'InfoIcon'}
                  />
                </span>
              </>
            }
          />
        </ListItem>
      );
      if (isInbound) inboundRelations.push(listItem);
      else outboundRelations.push(listItem);
    }
  }

  const getItemAssessments = (assessmentItem: IAssessmentProps[]) => {
    if (item && assessmentItem) {
      return assessmentItem
        .filter((assessment) => !assessment.field.includes('summary'))
        .map((assessment) => {
          return (
            <TableRow key={assessment.field}>
              <TableCell>
                <MappedString mapKey={assessment.field} />
              </TableCell>
              <TableCell>
                <StatusChip
                  status={assessment.status}
                  key={assessment.field}
                  value={assessment.field}
                />
                {assessment.message}
              </TableCell>
            </TableRow>
          );
        });
    }
    return [];
  };

  const assessments = item
    ? landscapeContext.assessment?.results[item?.fullyQualifiedIdentifier]
    : null;
  const assessmentStatus = assessments ? getItemAssessments(assessments) : [];
  const frameworks: ReactElement | null = item ? getLabelsWithPrefix('framework', item) : null;
  const interfaces: ReactElement | null = item ? getInterfaces(item) : null;

  let network: ReactElement[] = [];
  item?.networks.forEach((networkValue) =>
    network.push(
      <ListItem key={networkValue}>
        <ListItemText primary={networkValue} />
      </ListItem>
    )
  );
  const networks =
    item?.networks && item?.networks.length ? <List dense={true}> {network}</List> : null;

  const changeTab = (event: React.ChangeEvent<{}>, newValue: number) => {
    setValue(newValue);
  };

  const labels = item ? getMappedLabels(item) : null;
  const extend = (
    <>
      {small ? (
        <IconButton onClick={() => setCompact(!compact)} size={'small'}>
          <MoreVertSharp />
        </IconButton>
      ) : null}
      {!sticky ? (
        <IconButton
          size={'small'}
          onClick={() => {
            setItem(undefined);
            setVisible(false);
          }}
        >
          <Close />
        </IconButton>
      ) : null}
    </>
  );
  const assessmentSummary = item
    ? landscapeContext.getAssessmentSummary(item.fullyQualifiedIdentifier)
    : null;
  const tags =
    item?.tags && item?.tags.length
      ? item.tags.map((tagValue) => (
          <Chip size={'small'} label={tagValue} key={tagValue} className={extraClasses.tag} />
        ))
      : null;
  if (!visible) return null;

  return (
    <Card className={classes.card}>
      <CardHeader
        title={item ? item.name || item.identifier : null}
        titleTypographyProps={{ title: 'ID: ' + item?.fullyQualifiedIdentifier }}
        subheader={tags}
        avatar={
          item ? (
            <>
              <IconButton
                onClick={() => {
                  locateFunctionContext.locateFunction(item.fullyQualifiedIdentifier);
                }}
                size={'small'}
                title={'Click to locate'}
              >
                <ItemAvatar
                  item={item}
                  statusColor={assessmentSummary ? assessmentSummary.status : ''}
                />
              </IconButton>
            </>
          ) : (
            ''
          )
        }
        className={classes.cardHeader}
        action={<React.Fragment>{extend}</React.Fragment>}
      />

      {!compact ? (
        <div>
          <AppBar position={'static'}>
            <Tabs value={value} onChange={changeTab} variant={'fullWidth'} aria-label={'item tabs'}>
              <Tab
                icon={<Info />}
                label={<MappedString mapKey={'info'} />}
                style={{ minWidth: 50 }}
                title={'Info'}
                {...a11yProps(0, 'item')}
              />
              <Tab
                icon={<Power />}
                label={<MappedString mapKey={'relations'} />}
                style={{ minWidth: 50 }}
                title={'Relations'}
                {...a11yProps(1, 'item')}
              />
              <Tab
                icon={<Details />}
                label={<MappedString mapKey={'details'} />}
                title={'API / Interfaces'}
                style={{ minWidth: 50 }}
                {...a11yProps(2, 'item')}
              />
            </Tabs>
          </AppBar>
          <CardContent>
            <TabPanel value={value} index={0} prefix={'item'}>
              <Table aria-label={'info table'} style={{ tableLayout: 'fixed' }}>
                <TableBody>
                  {item?.group ? (
                    <TableRow key={'group'}>
                      <TableCell style={{ width: '33%' }}>
                        <MappedString mapKey={'Group'} />
                      </TableCell>
                      <TableCell>{item?.group}</TableCell>
                    </TableRow>
                  ) : null}
                  {item?.type ? (
                    <TableRow key={'type'}>
                      <TableCell>
                        <MappedString mapKey={'Type'} />
                      </TableCell>
                      <TableCell>{item?.type}</TableCell>
                    </TableRow>
                  ) : null}
                  {item?.description ? (
                    <TableRow key={'description'}>
                      <TableCell>
                        <MappedString mapKey={'Info'} />
                      </TableCell>
                      <TableCell>{item?.description}</TableCell>
                    </TableRow>
                  ) : null}
                  {item?.owner ? (
                    <TableRow key={'owner'}>
                      <TableCell>
                        <MappedString mapKey={'Owner'} />
                      </TableCell>
                      <TableCell>{item?.owner}</TableCell>
                    </TableRow>
                  ) : null}
                  {item?.contact ? (
                    <TableRow key={'contact'}>
                      <TableCell>
                        <MappedString mapKey={'Contact'} />
                      </TableCell>
                      <TableCell>{item?.contact}</TableCell>
                    </TableRow>
                  ) : null}
                  {item?.address ? (
                    <TableRow key={'address'}>
                      <TableCell>
                        <MappedString mapKey={'Address'} />
                      </TableCell>
                      <TableCell>{item?.address}</TableCell>
                    </TableRow>
                  ) : null}

                  {item && item?._links
                    ? Object.entries(item?._links).map((data) => {
                        if (data[0] === 'self') return null;
                        return (
                          <TableRow key={'link_' + data[0]}>
                            <TableCell>
                              <MappedString mapKey={data[0]} />
                            </TableCell>
                            <TableCell>
                              <Link
                                href={data[1].href}
                                className={classes.link}
                                style={{
                                  whiteSpace: 'normal',
                                  wordWrap: 'break-word',
                                }}
                              >
                                {data[1].href}
                              </Link>
                            </TableCell>
                          </TableRow>
                        );
                      })
                    : null}
                </TableBody>
              </Table>

              {assessmentStatus.length > 0 ? (
                <>
                  <br />
                  <Typography variant={'h6'}>
                    <MappedString mapKey={'Status'} />
                  </Typography>
                  <Table>
                    <TableBody>{assessmentStatus}</TableBody>
                  </Table>
                </>
              ) : null}
            </TabPanel>

            <TabPanel value={value} index={1} prefix={'item'}>
              {inboundRelations && inboundRelations.length ? (
                <div>
                  <Typography variant={'h6'}>
                    <MappedString mapKey={'Inbound'} />
                  </Typography>
                  <List dense={true}>{inboundRelations}</List>
                </div>
              ) : (
                ''
              )}
              {outboundRelations && outboundRelations.length ? (
                <div>
                  <Typography variant={'h6'}>
                    <MappedString mapKey={'Outbound'} />
                  </Typography>
                  <List dense={true}>{outboundRelations}</List>
                </div>
              ) : (
                ''
              )}
            </TabPanel>

            <TabPanel value={value} index={2} prefix={'item'}>
              {frameworks ? (
                <div className='frameworks'>
                  <Typography variant={'h6'}>
                    <MappedString mapKey={'Frameworks'} />
                  </Typography>
                  {frameworks}
                </div>
              ) : null}

              {networks ? (
                <div className='networks'>
                  <Typography variant={'h6'}>
                    <MappedString mapKey={'Networks'} />
                  </Typography>
                  {networks}
                </div>
              ) : null}

              {labels ? (
                <>
                  <Typography variant={'h6'}>
                    <MappedString mapKey={'Labels'} />
                  </Typography>
                  {labels}
                </>
              ) : null}

              {interfaces != null ? (
                <div className='interfaces'>
                  <Typography variant={'h6'}>
                    <MappedString mapKey={'Interfaces'} />
                  </Typography>
                  {interfaces}
                </div>
              ) : null}
            </TabPanel>
          </CardContent>
        </div>
      ) : null}
    </Card>
  );
};
export default Item;
