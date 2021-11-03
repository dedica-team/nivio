import React, {ReactElement, useContext, useEffect, useState} from 'react';
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
} from '@material-ui/core';
import {get} from '../../../../utils/API/APIClient';
import CardContent from '@material-ui/core/CardContent';
import {IAssessmentProps, IItem} from '../../../../interfaces';
import {getItem, getLabelsWithPrefix, getMappedLabels} from '../../Utils/utils';
import StatusChip from '../../../StatusChip/StatusChip';
import IconButton from '@material-ui/core/IconButton';
import {Close, Details, ExpandMore, Info, MoreVertSharp, Power} from '@material-ui/icons';
import Chip from '@material-ui/core/Chip';
import {createStyles, makeStyles} from '@material-ui/core/styles';
import {LocateFunctionContext} from '../../../../Context/LocateFunctionContext';
import componentStyles from '../../../../Resources/styling/ComponentStyles';
import ItemAvatar from './ItemAvatar';
import {LandscapeContext} from '../../../../Context/LandscapeContext';
import {a11yProps, TabPanel} from '../../Utils/TabUtils';

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
    fullyQualifiedItemIdentifier?: string;
}

/**
 * Returns a chosen Landscape item if information is available
 *
 *
 */
const Item: React.FC<Props> = ({fullyQualifiedItemIdentifier, small}) => {
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

    const getInterfaces = (element: IItem): ReactElement | null => {
        if (!element?.interfaces) return null;
        let ifaceElements: ReactElement[] = [];
        element.interfaces.forEach((iface, key) => {
            ifaceElements.push(
                <Accordion key={key} className={extraClasses.interfaces}>
                    <AccordionSummary
                        expandIcon={<ExpandMore/>}
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
                }}
            >
              {iface.name || iface.path}
            </span>
                    </AccordionSummary>
                    <AccordionDetails>
                        {iface.summary ? (
                            <div>
                                {iface.summary}
                                <br/>
                                <br/>
                            </div>
                        ) : null}
                        {iface.description ? (
                            <div>
                                {iface.description}
                                <br/>
                                <br/>
                            </div>
                        ) : null}
                        Path: {iface.path || '-'}
                        <br/>
                        <br/>
                        Params: {iface.parameters || '-'}
                        <br/>
                        <br/>
                        Format: {iface.format || '-'}
                        <br/>
                        <br/>
                        Payload: {iface.payload || '-'}
                        <br/>
                        <br/>
                        Protection: {iface.protection || '-'}
                        <br/>
                        <br/>
                        Deprecated: {iface.deprecated ? 'Yes' : '-'}
                        <br/>
                        <br/>
                    </AccordionDetails>
                </Accordion>
            );
        });

        if (ifaceElements.length === 0) return null;
        return <List dense={true}>{ifaceElements}</List>;
    };

    useEffect(() => {
        if (!item && fullyQualifiedItemIdentifier) {
            get(`/api/${fullyQualifiedItemIdentifier}`).then((loaded) => {
                setItem(loaded);
            });
        }
    }, [item, fullyQualifiedItemIdentifier]);

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
                            <ItemAvatar item={other} statusColor={status?.status || ''}/>
                        </IconButton>
                    </ListItemIcon>
                    <ListItemText primary={primary} secondary={secondary}/>
                </ListItem>
            );
            if (isInbound) inboundRelations.push(listItem);
            else outboundRelations.push(listItem);
        }
    }

    const getItemAssessments = (assessmentItem: IAssessmentProps[]) => {
        if (item && assessmentItem) {
            return assessmentItem
                .filter((assessment) => !assessment.field.includes('summary.'))
                .map((assessment) => {
                    return (
                        <TableRow key={assessment.field}>
                            <TableCell>{assessment.field}</TableCell>
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

    const changeTab = (event: React.ChangeEvent<{}>, newValue: number) => {
        setValue(newValue);
    };

    const labels = item ? getMappedLabels(item) : null;
    const extend = (
        <>
            {small ? (
                <IconButton onClick={() => setCompact(!compact)} size={'small'}>
                    <MoreVertSharp/>
                </IconButton>
            ) : null}
            <IconButton
                size={'small'}
                onClick={() => {
                    setItem(undefined);
                    setVisible(false);
                }}
            >
                <Close/>
            </IconButton>
        </>
    );
    const assessmentSummary = item
        ? landscapeContext.getAssessmentSummary(item.fullyQualifiedIdentifier)
        : null;
    const tags =
        item?.tags && item?.tags.length
            ? item.tags.map((value) => (
                <Chip size={'small'} label={value} key={value} className={extraClasses.tag}/>
            ))
            : null;

    if (!visible) return null;

    return (
        <Card className={classes.card}>
            <CardHeader
                title={item ? item.name || item.identifier : null}
                titleTypographyProps={{title: 'ID: ' + item?.fullyQualifiedIdentifier}}
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
                                icon={<Info/>}
                                label={'info'}
                                style={{minWidth: 50}}
                                title={'Info'}
                                {...a11yProps(0, 'item')}
                            />
                            <Tab
                                icon={<Power/>}
                                label={'relations'}
                                style={{minWidth: 50}}
                                title={'Relations'}
                                {...a11yProps(1, 'item')}
                            />
                            <Tab
                                icon={<Details/>}
                                label={'Details'}
                                title={'API / Interfaces'}
                                style={{minWidth: 50}}
                                {...a11yProps(2, 'item')}
                            />
                        </Tabs>
                    </AppBar>
                    <CardContent>
                        <TabPanel value={value} index={0} prefix={'item'}>
                            <Table aria-label={'info table'} style={{tableLayout: 'fixed'}}>
                                <TableBody>
                                    {item?.group ? (
                                        <TableRow key={'group'}>
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
                                    {item?.address ? (
                                        <TableRow key={'address'}>
                                            <TableCell>Address</TableCell>
                                            <TableCell>{item?.address}</TableCell>
                                        </TableRow>
                                    ) : null}

                                    {item && item?._links
                                        ? Object.entries(item?._links).map((data) => {
                                            if (data[0] === 'self') return null;
                                            return (
                                                <TableRow key={'link_' + data[0]}>
                                                    <TableCell>{data[0]}</TableCell>
                                                    <TableCell>
                                                        <Link href={data[1].href} className={classes.link}>
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
                                    <br/>
                                    <Typography variant={'h6'}>Status</Typography>
                                    <Table>
                                        <TableBody>{assessmentStatus}</TableBody>
                                    </Table>
                                </>
                            ) : null}
                        </TabPanel>

                        <TabPanel value={value} index={1} prefix={'item'}>
                            {inboundRelations && inboundRelations.length ? (
                                <div>
                                    <Typography variant={'h6'}>Inbound</Typography>
                                    <List dense={true}>{inboundRelations}</List>
                                </div>
                            ) : (
                                ''
                            )}
                            {outboundRelations && outboundRelations.length ? (
                                <div>
                                    <Typography variant={'h6'}>Outbound</Typography>
                                    <List dense={true}>{outboundRelations}</List>
                                </div>
                            ) : (
                                ''
                            )}
                        </TabPanel>

                        <TabPanel value={value} index={2} prefix={'item'}>
                            {frameworks ? (
                                <div className='frameworks'>
                                    <Typography variant={'h6'}>Frameworks</Typography>
                                    {frameworks}
                                </div>
                            ) : null}

                            {labels ? (
                                <>
                                    <Typography variant={'h6'}>Labels</Typography>
                                    {labels}
                                </>
                            ) : null}

                            {interfaces != null ? (
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