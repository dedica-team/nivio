import React, { useState, ReactElement, useEffect, useContext } from 'react';
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  AppBar, Box,
  Card,
  CardHeader, Link,
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
  Typography
} from "@material-ui/core";
import { get } from '../../../../utils/API/APIClient';
import CardContent from '@material-ui/core/CardContent';
import { IAssessmentProps, IItem } from '../../../../interfaces';
import { getItemIcon, getLabels } from "../../Utils/utils";
import StatusChip from '../../../StatusChip/StatusChip';
import IconButton from '@material-ui/core/IconButton';
import {
  Details,
  ExpandMore,
  FilterCenterFocus,
  Info,
  MoreVertSharp,
  Wifi
} from "@material-ui/icons";
import Chip from '@material-ui/core/Chip';
import { createStyles, makeStyles } from '@material-ui/core/styles';
import Avatar from '@material-ui/core/Avatar';
import { LocateFunctionContext } from '../../../../Context/LocateFunctionContext';
import componentStyles from '../../../../Resources/styling/ComponentStyles';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    labels: {
      backgroundColor: theme.palette.primary.main,
    },
    tag: {
      backgroundColor: theme.palette.primary.main,
    },
    interfaces: {
      backgroundColor: theme.palette.primary.main,
    },
  })
);

interface Props {
  small?: boolean;
  useItem?: IItem;
  fullyQualifiedItemIdentifier?: string;
}

/**
 * Returns a chosen Landscape item if information is available
 *
 *
 */
const Item: React.FC<Props> = ({ useItem, fullyQualifiedItemIdentifier, small }) => {
  const [assessment, setAssessment] = useState<IAssessmentProps[] | undefined>(undefined);
  const [item, setItem] = useState<IItem | undefined>(undefined);
  const [compact, setCompact] = useState<boolean>(false);
  const [value, setValue] = React.useState(0);
  const locateFunctionContext = useContext(LocateFunctionContext);

  const classes = componentStyles();
  const extraClasses = useStyles();
  let relations: ReactElement[] = [];

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
            <span title={iface.name || iface.path} style={{width: 200, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace:'nowrap'}}>{iface.name || iface.path}</span>
          </AccordionSummary>
          <AccordionDetails>
            {iface.summary ? (<div>{iface.summary}<br /><br /></div>): null}
            {iface.description ? (<div>{iface.description}<br /><br /></div>): null}
            Path: {iface.path || '-'}<br /><br />
            Params: {iface.parameters || '-'}<br /><br />
            Format: {iface.format || '-'}<br /><br />
            Payload: {iface.payload || '-'}<br /><br />
            Protection: {iface.protection || '-'}<br /><br />
            Deprecated: {iface.deprecated ? 'Yes' : '-'}<br /><br />
          </AccordionDetails>
        </Accordion>
      );
    });

    return <List dense={true}>{ifaceElements}</List>;
  }


  useEffect(() => {
    const loadAssessment = (item: IItem) => {
      const landscapeIdentifier = item ? item.fullyQualifiedIdentifier.split('/') : [];

      if (landscapeIdentifier[0]) {
        //TODO load direct to with fqi in path
        get(`/assessment/${landscapeIdentifier[0]}`).then((response) => {
          if (response) {
            // @ts-ignore
            setAssessment(response.results[item?.fullyQualifiedIdentifier]);
          }
        });
      }
    };

    const reset = (item: IItem) => {
      setItem(item);
      setAssessment(undefined);
      loadAssessment(item);
    };

    if (useItem) {
      if (useItem && item !== useItem) {
        reset(useItem);
      }
    } else {
      if (!item && fullyQualifiedItemIdentifier) {
        get(`/api/${fullyQualifiedItemIdentifier}`).then((loaded) => {
          reset(loaded);
        });
      }
    }

    if (small) {
      setCompact(true);
    }
  }, [item, fullyQualifiedItemIdentifier, useItem, small, assessment]);

  if (item) {
    for (let key of Object.keys(item.relations)) {

      let relation = item.relations[key];
      const isInbound = relation.direction === 'inbound';
      const primary = `${relation.name}`;
      let secondary = `${relation.description || ''} (${relation.type ? relation.type : ''} ${relation.direction})`;
      if (relation.format) secondary += ', format: ' + relation.format;
      relations.push(
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
            >
              <FilterCenterFocus />
            </IconButton>
          </ListItemIcon>
          <ListItemText primary={primary} secondary={secondary} />
        </ListItem>
      );
    }
  }

  const getItemAssessments = (assessmentItem: IAssessmentProps[]) => {
    if (item && assessmentItem) {
      return assessmentItem
        .filter((item) => !item.field.includes('summary.'))
        .map((item) => {
          return (
            <TableRow key={item.field} >
              <TableCell>{item.field}</TableCell>
              <TableCell><StatusChip
                status={item.status}
                key={item.field}
                value={item.message}
              />
              </TableCell>
            </TableRow>
          );
        });
    }
    return [];
  };

  const assessmentStatus = assessment ? getItemAssessments(assessment) : [];
  const interfaces: ReactElement | null = item ? getInterfaces(item) : null;

  const findButton =
    locateFunctionContext.locateFunction && item ? (
      <IconButton
        onClick={() => {
          locateFunctionContext.locateFunction(item.fullyQualifiedIdentifier);
        }}
      >
        <FilterCenterFocus />
      </IconButton>
    ) : null;

  const a11yProps = (index: any) => {
    return {
      'id': `simple-tab-${index}`,
      'aria-controls': `simple-tabpanel-${index}`,
    };
  };

  const changeTab = (event: React.ChangeEvent<{}>, newValue: number) => {
    setValue(newValue);
  };

  interface TabPanelProps {
    children?: React.ReactNode;
    index: any;
    value: any;
  }

  function TabPanel(props: TabPanelProps) {
    const { children, value, index, ...other } = props;

    return (
      <div
        role='tabpanel'
        hidden={value !== index}
        id={`simple-tabpanel-${index}`}
        aria-labelledby={`simple-tab-${index}`}
        {...other}
      >
        {value === index && <Box>{children}</Box>}
      </div>
    );
  }

  const labels = item ? getLabels(item) : null;
  const extend = small ? (
    <IconButton onClick={() => setCompact(!compact)}>
      <MoreVertSharp />
    </IconButton>
  ) : null;
  return (
    <Card className={classes.card}>
      <CardHeader
        title={item ? item.name || item.identifier : null}
        titleTypographyProps={{ title: 'ID: ' + item?.fullyQualifiedIdentifier }}
        avatar={
          item ? (
            <Avatar
              imgProps={{ style: { objectFit: 'contain' } }}
              src={getItemIcon(item)}
              style={{
                backgroundColor: 'rgba(255, 255, 255, 0.75)',
                border: '2px solid #' + item.color,
              }}
            />
          ) : (
              ''
            )
        }
        className={classes.cardHeader}
        action={
          <React.Fragment>
            {extend}
            {findButton}
          </React.Fragment>
        }
      />

      {!compact ? (
        <div>
          <AppBar position='static'>
            <Tabs
              value={value}
              onChange={changeTab}
              variant={'fullWidth'}
              aria-label='simple tabs example'
            >
              <Tab icon={<Info />} label={'info'} style={{ minWidth: 50 }} title={'Info'} {...a11yProps(0)} />
              <Tab icon={<Wifi />} label={'relations'} style={{ minWidth: 50 }} title={'Relations'} {...a11yProps(1)} />
              <Tab
                icon={<Details />} label={'Details'}
                title={'API / Interfaces'}
                style={{ minWidth: 50 }}
                {...a11yProps(2)}
              />
            </Tabs>
          </AppBar>
          <CardContent>
            <TabPanel value={value} index={0}>
              <Table aria-label={'info table'} style={{ tableLayout: 'fixed'}}>
                <TableBody>
                  {item?.group ? (
                    <TableRow key={'group'} >
                      <TableCell style={{width: '33%'}}>Group</TableCell>
                      <TableCell>{item?.group}</TableCell>
                    </TableRow>
                  ) : null}
                  {item?.type ? (
                    <TableRow key={'type'}>
                      <TableCell>Type</TableCell>
                      <TableCell>{item?.type}</TableCell>
                    </TableRow>
                  ) : null}
                  {item?.description ? (
                    <TableRow key={'description'}>
                      <TableCell>Info</TableCell>
                      <TableCell>{item?.description}</TableCell>
                    </TableRow>
                  ) : null}
                  {item?.owner ? (
                    <TableRow key={'owner'}>
                      <TableCell>Owner</TableCell>
                      <TableCell>{item?.owner}</TableCell>
                    </TableRow>
                  ) : null}
                  {item?.contact ? (
                    <TableRow key={'contact'}>
                      <TableCell>Contact</TableCell>
                      <TableCell>{item?.contact}</TableCell>
                    </TableRow>
                  ) : null}
                  { (item?.tags && item?.tags.length) ? (
                    <TableRow key={'tags'}>
                      <TableCell>Tags</TableCell>
                      <TableCell>{item.tags.map((value) => (
                        <Chip size='small' label={value} key={value} className={extraClasses.tag} />
                      ))}</TableCell>
                    </TableRow>
                  ) : null}

                  { (item && item?._links) ?
                    Object.entries(item?._links).map((data) => {
                      if (data[0] === 'self') return null;
                      return <TableRow key={'link_' + data[0]}>
                        <TableCell>{data[0]}</TableCell>
                        <TableCell><Link href={data[1].href}>{data[1].href}</Link></TableCell>
                      </TableRow>

                    }) : null
                  }

                  {assessmentStatus.length > 0 ? assessmentStatus : null}

                </TableBody>
              </Table>

              {labels ? (
                <Accordion className={extraClasses.labels}>
                  <AccordionSummary
                    expandIcon={<ExpandMore />}
                    aria-controls='panel_labels-content'
                    id='panel_labels-header'
                  >
                    more
                  </AccordionSummary>
                  <AccordionDetails>{labels}</AccordionDetails>
                </Accordion>
              ) : null}

            </TabPanel>

            <TabPanel value={value} index={1}>
              {relations && relations.length ? (
                  <List dense={true}>{relations}</List>
              ) : (
                ''
              )}
            </TabPanel>

            <TabPanel value={value} index={2}>
              {interfaces ? (
                <div className='interfaces'>
                  <Typography variant={'h6'}>Interfaces</Typography>
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
